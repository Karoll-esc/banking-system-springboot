package com.sofka.banking.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.request.UpdateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.exception.usuario.CedulaAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.EmailAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.UsuarioNotFoundException;
import com.sofka.banking.system.mapper.UsuarioMapper;
import com.sofka.banking.system.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

        @Mock
        private UsuarioRepository usuarioRepository;

        @Mock
        private UsuarioMapper usuarioMapper;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private UsuarioServiceImpl usuarioService;

        private Usuario usuario;
        private UsuarioDTO usuarioDTO;
        private CreateUsuarioDTO createUsuarioDTO;
        private UpdateUsuarioDTO updateUsuarioDTO;

        @BeforeEach
        void setUp() {
                usuario = Usuario.builder().id(1L).cedula("12345678").nombre("Juan")
                                .apellido("Pérez").email("juan@email.com").telefono("1234567890")
                                .build();

                usuarioDTO = UsuarioDTO.builder().id(1L).cedula("12345678").nombre("Juan")
                                .apellido("Pérez").email("juan@email.com").telefono("1234567890")
                                .build();

                createUsuarioDTO = CreateUsuarioDTO.builder().cedula("12345678").nombre("Juan")
                                .apellido("Pérez").email("juan@email.com").telefono("1234567890")
                                .password("Password123!").build();

                updateUsuarioDTO = UpdateUsuarioDTO.builder().nombre("Juan Carlos")
                                .apellido("Pérez García").email("juan.carlos@email.com")
                                .telefono("0987654321").build();
        }

        @Test
        void crearUsuario_ConDatosValidos_DeberiaCrearUsuario() {
                // Given
                when(usuarioRepository.existsByCedula(createUsuarioDTO.getCedula()))
                                .thenReturn(false);
                when(usuarioRepository.existsByEmail(createUsuarioDTO.getEmail()))
                                .thenReturn(false);
                when(usuarioMapper.toEntity(createUsuarioDTO)).thenReturn(usuario);
                when(passwordEncoder.encode(createUsuarioDTO.getPassword()))
                                .thenReturn("$2a$10$hashedPassword");
                when(usuarioRepository.save(usuario)).thenReturn(usuario);
                when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

                // When
                UsuarioDTO resultado = usuarioService.crearUsuario(createUsuarioDTO);

                // Then
                assertAll("Usuario creado exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertInstanceOf(UsuarioDTO.class, resultado,
                                                "Debe ser una instancia de UsuarioDTO"),
                                () -> assertEquals(usuarioDTO.getId(), resultado.getId(),
                                                "El ID debe coincidir"),
                                () -> assertEquals(usuarioDTO.getCedula(), resultado.getCedula(),
                                                "La cédula debe coincidir"),
                                () -> assertEquals(usuarioDTO.getNombre(), resultado.getNombre(),
                                                "El nombre debe coincidir"),
                                () -> assertEquals(usuarioDTO.getEmail(), resultado.getEmail(),
                                                "El email debe coincidir"));

                verify(usuarioRepository).existsByCedula(createUsuarioDTO.getCedula());
                verify(usuarioRepository).existsByEmail(createUsuarioDTO.getEmail());
                verify(usuarioMapper).toEntity(createUsuarioDTO);
                verify(usuarioRepository).save(usuario);
                verify(usuarioMapper).toDTO(usuario);
                verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        void crearUsuario_ConCedulaExistente_DeberiaLanzarExcepcion() {
                // Given
                when(usuarioRepository.existsByCedula(createUsuarioDTO.getCedula()))
                                .thenReturn(true);

                // When
                CedulaAlreadyExistsException exception = assertThrows(
                                CedulaAlreadyExistsException.class,
                                () -> usuarioService.crearUsuario(createUsuarioDTO));

                // Then
                assertAll("Validación de excepción por cédula duplicada",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals(
                                                "Ya existe un usuario con la cédula: "
                                                                + createUsuarioDTO.getCedula(),
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(usuarioRepository).existsByCedula(createUsuarioDTO.getCedula());
                verify(usuarioRepository, never()).existsByEmail(any());
                verify(usuarioRepository, never()).save(any());
                verifyNoInteractions(usuarioMapper);
        }

        @Test
        void crearUsuario_ConEmailExistente_DeberiaLanzarExcepcion() {
                // Given
                when(usuarioRepository.existsByCedula(createUsuarioDTO.getCedula()))
                                .thenReturn(false);
                when(usuarioRepository.existsByEmail(createUsuarioDTO.getEmail())).thenReturn(true);

                // When
                EmailAlreadyExistsException exception = assertThrows(
                                EmailAlreadyExistsException.class,
                                () -> usuarioService.crearUsuario(createUsuarioDTO));

                // Then
                assertAll("Validación de excepción por email duplicado",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals(
                                                "Ya existe un usuario con el email: "
                                                                + createUsuarioDTO.getEmail(),
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(usuarioRepository).existsByCedula(createUsuarioDTO.getCedula());
                verify(usuarioRepository).existsByEmail(createUsuarioDTO.getEmail());
                verify(usuarioRepository, never()).save(any());
                verifyNoInteractions(usuarioMapper);
        }

        @Test
        void obtenerTodosLosUsuarios_ConUsuariosExistentes_DeberiaRetornarLista() {
                // Given
                Usuario usuario2 = Usuario.builder().id(2L).cedula("87654321").nombre("María")
                                .apellido("García").email("maria@email.com").telefono("0987654321")
                                .build();

                UsuarioDTO usuarioDTO2 = UsuarioDTO.builder().id(2L).cedula("87654321")
                                .nombre("María").apellido("García").email("maria@email.com")
                                .telefono("0987654321").build();

                List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
                List<UsuarioDTO> usuariosDTO = Arrays.asList(usuarioDTO, usuarioDTO2);

                when(usuarioRepository.findAll()).thenReturn(usuarios);
                when(usuarioMapper.toDTOList(usuarios)).thenReturn(usuariosDTO);

                // When
                List<UsuarioDTO> resultado = usuarioService.obtenerTodosLosUsuarios();

                // Then
                assertAll("Lista de usuarios obtenida exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(2, resultado.size(), "Debe retornar 2 usuarios"),
                                () -> assertEquals(usuarioDTO.getId(), resultado.get(0).getId(),
                                                "El primer usuario debe coincidir"),
                                () -> assertEquals(usuarioDTO2.getId(), resultado.get(1).getId(),
                                                "El segundo usuario debe coincidir"));

                verify(usuarioRepository).findAll();
                verify(usuarioMapper).toDTOList(usuarios);
                verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        void obtenerTodosLosUsuarios_SinUsuarios_DeberiaRetornarListaVacia() {
                // Given
                List<Usuario> usuarios = Arrays.asList();
                List<UsuarioDTO> usuariosDTO = Arrays.asList();

                when(usuarioRepository.findAll()).thenReturn(usuarios);
                when(usuarioMapper.toDTOList(usuarios)).thenReturn(usuariosDTO);

                // When
                List<UsuarioDTO> resultado = usuarioService.obtenerTodosLosUsuarios();

                // Then
                assertAll("Lista vacía retornada correctamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertTrue(resultado.isEmpty(), "La lista debe estar vacía"));

                verify(usuarioRepository).findAll();
                verify(usuarioMapper).toDTOList(usuarios);
                verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        void obtenerUsuarioPorId_ConIdExistente_DeberiaRetornarUsuario() {
                // Given
                Long id = 1L;
                when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
                when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

                // When
                UsuarioDTO resultado = usuarioService.obtenerUsuarioPorId(id);

                // Then
                assertAll("Usuario obtenido por ID exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(usuarioDTO.getId(), resultado.getId(),
                                                "El ID debe coincidir"),
                                () -> assertEquals(usuarioDTO.getCedula(), resultado.getCedula(),
                                                "La cédula debe coincidir"),
                                () -> assertEquals(usuarioDTO.getNombre(), resultado.getNombre(),
                                                "El nombre debe coincidir"));

                verify(usuarioRepository).findById(id);
                verify(usuarioMapper).toDTO(usuario);
                verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        void obtenerUsuarioPorId_ConIdInexistente_DeberiaLanzarExcepcion() {
                // Given
                Long id = 999L;
                when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

                // When
                UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class,
                                () -> usuarioService.obtenerUsuarioPorId(id));

                // Then
                assertAll("Validación de excepción por usuario no encontrado",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals("Usuario con ID " + id + " no encontrado",
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(usuarioRepository).findById(id);
                verifyNoInteractions(usuarioMapper);
                verifyNoMoreInteractions(usuarioRepository);
        }

        @Test
        void actualizarUsuario_ConDatosValidos_DeberiaActualizarUsuario() {
                // Given
                Long id = 1L;
                Usuario usuarioActualizado = Usuario.builder().id(1L).cedula("12345678")
                                .nombre("Juan Carlos").apellido("Pérez García")
                                .email("juan.carlos@email.com").telefono("0987654321").build();

                UsuarioDTO usuarioDTOActualizado = UsuarioDTO.builder().id(1L).cedula("12345678")
                                .nombre("Juan Carlos").apellido("Pérez García")
                                .email("juan.carlos@email.com").telefono("0987654321").build();

                when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
                when(usuarioRepository.save(usuario)).thenReturn(usuarioActualizado);
                when(usuarioMapper.toDTO(usuarioActualizado)).thenReturn(usuarioDTOActualizado);

                // When
                UsuarioDTO resultado = usuarioService.actualizarUsuario(id, updateUsuarioDTO);

                // Then
                assertAll("Usuario actualizado exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(usuarioDTOActualizado.getId(), resultado.getId(),
                                                "El ID debe coincidir"),
                                () -> assertEquals(usuarioDTOActualizado.getNombre(),
                                                resultado.getNombre(),
                                                "El nombre debe estar actualizado"),
                                () -> assertEquals(usuarioDTOActualizado.getApellido(),
                                                resultado.getApellido(),
                                                "El apellido debe estar actualizado"),
                                () -> assertEquals(usuarioDTOActualizado.getEmail(),
                                                resultado.getEmail(),
                                                "El email debe estar actualizado"));

                verify(usuarioRepository).findById(id);
                verify(usuarioMapper).updateEntityFromDTO(usuario, updateUsuarioDTO);
                verify(usuarioRepository).save(usuario);
                verify(usuarioMapper).toDTO(usuarioActualizado);
                verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        void actualizarUsuario_ConIdInexistente_DeberiaLanzarExcepcion() {
                // Given
                Long id = 999L;
                when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

                // When
                UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class,
                                () -> usuarioService.actualizarUsuario(id, updateUsuarioDTO));

                // Then
                assertAll("Validación de excepción por usuario no encontrado en actualización",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals("Usuario con ID " + id + " no encontrado",
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(usuarioRepository).findById(id);
                verify(usuarioMapper, never()).updateEntityFromDTO(any(), any());
                verify(usuarioRepository, never()).save(any());
                verifyNoMoreInteractions(usuarioRepository);
                verifyNoInteractions(usuarioMapper);
        }

        @Test
        void eliminarUsuario_ConIdExistente_DeberiaEliminarUsuario() {
                // Given
                Long id = 1L;
                Usuario usuarioAEliminar = Usuario.builder().id(id).cedula("12345678")
                                .nombre("Juan").apellido("Pérez").email("juan@email.com")
                                .telefono("+573001234567").build();
                when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioAEliminar));

                // When
                String resultado = usuarioService.eliminarUsuario(id);

                // Then
                assertAll("Usuario eliminado exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals("Usuario eliminado exitosamente", resultado,
                                                "El mensaje debe ser correcto"));

                verify(usuarioRepository).findById(id);
                verify(usuarioRepository).delete(usuarioAEliminar);
                verifyNoMoreInteractions(usuarioRepository);
                verifyNoInteractions(usuarioMapper);
        }

        @Test
        void eliminarUsuario_ConIdInexistente_DeberiaLanzarExcepcion() {
                // Given
                Long id = 999L;
                when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

                // When
                UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class,
                                () -> usuarioService.eliminarUsuario(id));

                // Then
                assertAll("Validación de excepción por usuario no encontrado en eliminación",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals("Usuario con ID " + id + " no encontrado",
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(usuarioRepository).findById(id);
                verify(usuarioRepository, never()).delete(any());
                verifyNoMoreInteractions(usuarioRepository);
                verifyNoInteractions(usuarioMapper);
        }
}
