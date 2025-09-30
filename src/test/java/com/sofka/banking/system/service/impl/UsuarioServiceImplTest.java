package com.sofka.banking.system.service.impl;

import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.exception.usuario.CedulaAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.EmailAlreadyExistsException;
import com.sofka.banking.system.mapper.UsuarioMapper;
import com.sofka.banking.system.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private CreateUsuarioDTO createUsuarioDTO;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .cedula("12345678")
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@email.com")
                .telefono("1234567890")
                .build();

        usuarioDTO = UsuarioDTO.builder()
                .id(1L)
                .cedula("12345678")
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@email.com")
                .telefono("1234567890")
                .build();

        createUsuarioDTO = CreateUsuarioDTO.builder()
                .cedula("12345678")
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@email.com")
                .telefono("1234567890")
                .build();
    }

    @Test
    void crearUsuario_ConDatosValidos_DeberiaCrearUsuario() {
        // Given
        when(usuarioRepository.existsByCedula(createUsuarioDTO.getCedula())).thenReturn(false);
        when(usuarioRepository.existsByEmail(createUsuarioDTO.getEmail())).thenReturn(false);
        when(usuarioMapper.toEntity(createUsuarioDTO)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        // When
        UsuarioDTO resultado = usuarioService.crearUsuario(createUsuarioDTO);

        // Then
        assertAll("Usuario creado exitosamente",
                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                () -> assertInstanceOf(UsuarioDTO.class, resultado, "Debe ser una instancia de UsuarioDTO"),
                () -> assertEquals(usuarioDTO.getId(), resultado.getId(), "El ID debe coincidir"),
                () -> assertEquals(usuarioDTO.getCedula(), resultado.getCedula(), "La cédula debe coincidir"),
                () -> assertEquals(usuarioDTO.getNombre(), resultado.getNombre(), "El nombre debe coincidir"),
                () -> assertEquals(usuarioDTO.getEmail(), resultado.getEmail(), "El email debe coincidir")
        );

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
        when(usuarioRepository.existsByCedula(createUsuarioDTO.getCedula())).thenReturn(true);

        // When
        CedulaAlreadyExistsException exception = assertThrows(
                CedulaAlreadyExistsException.class,
                () -> usuarioService.crearUsuario(createUsuarioDTO)
        );

        // Then
        assertAll("Validación de excepción por cédula duplicada",
                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                () -> assertEquals("Ya existe un usuario con la cédula: " + createUsuarioDTO.getCedula(),
                        exception.getMessage(), "El mensaje de error debe ser correcto")
        );

        verify(usuarioRepository).existsByCedula(createUsuarioDTO.getCedula());
        verify(usuarioRepository, never()).existsByEmail(any());
        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void crearUsuario_ConEmailExistente_DeberiaLanzarExcepcion() {
        // Given
        when(usuarioRepository.existsByCedula(createUsuarioDTO.getCedula())).thenReturn(false);
        when(usuarioRepository.existsByEmail(createUsuarioDTO.getEmail())).thenReturn(true);

        // When
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> usuarioService.crearUsuario(createUsuarioDTO)
        );

        // Then
        assertAll("Validación de excepción por email duplicado",
                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                () -> assertEquals("Ya existe un usuario con el email: " + createUsuarioDTO.getEmail(),
                        exception.getMessage(), "El mensaje de error debe ser correcto")
        );

        verify(usuarioRepository).existsByCedula(createUsuarioDTO.getCedula());
        verify(usuarioRepository).existsByEmail(createUsuarioDTO.getEmail());
        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(usuarioMapper);
    }
}