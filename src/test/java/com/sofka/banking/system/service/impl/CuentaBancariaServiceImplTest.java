package com.sofka.banking.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.exception.cuentaBancaria.NumeroCuentaAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.UsuarioNotFoundException;
import com.sofka.banking.system.mapper.CuentaBancariaMapper;
import com.sofka.banking.system.repository.CuentaBancariaRepository;
import com.sofka.banking.system.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CuentaBancariaServiceImplTest {

        @Mock
        private CuentaBancariaRepository cuentaBancariaRepository;

        @Mock
        private UsuarioRepository usuarioRepository;

        @Mock
        private CuentaBancariaMapper cuentaBancariaMapper;

        @InjectMocks
        private CuentaBancariaServiceImpl cuentaBancariaService;

        private Usuario usuario;
        private CuentaBancaria cuentaBancaria;
        private CuentaBancariaDTO cuentaBancariaDTO;
        private CreateCuentaBancariaDTO createCuentaBancariaDTO;

        @BeforeEach
        void setUp() {
                usuario = Usuario.builder().id(1L).cedula("12345678").nombre("Juan")
                                .apellido("Pérez").email("juan@email.com").telefono("1234567890")
                                .build();

                cuentaBancaria = CuentaBancaria.builder().id(1L).numeroCuenta("1234567890")
                                .saldoActual(new BigDecimal("1000.00")).usuario(usuario).build();

                cuentaBancariaDTO = CuentaBancariaDTO.builder().id(1L).numeroCuenta("1234567890")
                                .saldoActual(new BigDecimal("1000.00")).build();

                createCuentaBancariaDTO = CreateCuentaBancariaDTO.builder()
                                .numeroCuenta("1234567890").saldoActual(new BigDecimal("1000.00"))
                                .usuarioId(1L).build();
        }

        @Test
        void consultarSaldo_ConCuentaExistente_DeberiaRetornarSaldo() {
                // Given
                when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));
                when(cuentaBancariaMapper.toDTO(cuentaBancaria)).thenReturn(cuentaBancariaDTO);

                // When
                CuentaBancariaDTO resultado = cuentaBancariaService.consultarSaldo(1L);

                // Then
                assertNotNull(resultado);
                assertEquals(cuentaBancariaDTO.getSaldoActual(), resultado.getSaldoActual());
                assertEquals(cuentaBancariaDTO.getNumeroCuenta(), resultado.getNumeroCuenta());
                verify(cuentaBancariaRepository).findById(1L);
                verify(cuentaBancariaMapper).toDTO(cuentaBancaria);
        }

        @Test
        void consultarSaldo_ConCuentaInexistente_DeberiaLanzarExcepcion() {
                // Given
                when(cuentaBancariaRepository.findById(999L)).thenReturn(Optional.empty());

                // When & Then
                CuentaBancariaNotFoundException exception =
                                assertThrows(CuentaBancariaNotFoundException.class,
                                                () -> cuentaBancariaService.consultarSaldo(999L));

                assertEquals("Cuenta bancaria con ID 999 no encontrada", exception.getMessage());
                verify(cuentaBancariaRepository).findById(999L);
                verify(cuentaBancariaMapper, never()).toDTO(any());
        }

        @Test
        void crearCuenta_ConDatosValidos_DeberiaCrearCuenta() {
                // Given
                when(cuentaBancariaRepository
                                .existsByNumeroCuenta(createCuentaBancariaDTO.getNumeroCuenta()))
                                                .thenReturn(false);
                when(usuarioRepository.findById(createCuentaBancariaDTO.getUsuarioId()))
                                .thenReturn(Optional.of(usuario));
                when(cuentaBancariaRepository.save(any(CuentaBancaria.class)))
                                .thenReturn(cuentaBancaria);
                when(cuentaBancariaMapper.toDTO(cuentaBancaria)).thenReturn(cuentaBancariaDTO);

                // When
                CuentaBancariaDTO resultado =
                                cuentaBancariaService.crearCuenta(createCuentaBancariaDTO);

                // Then
                assertAll("Cuenta creada exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(cuentaBancariaDTO.getId(), resultado.getId(),
                                                "El ID debe coincidir"),
                                () -> assertEquals(cuentaBancariaDTO.getNumeroCuenta(),
                                                resultado.getNumeroCuenta(),
                                                "El número de cuenta debe coincidir"),
                                () -> assertEquals(cuentaBancariaDTO.getSaldoActual(),
                                                resultado.getSaldoActual(),
                                                "El saldo debe coincidir"));

                verify(cuentaBancariaRepository)
                                .existsByNumeroCuenta(createCuentaBancariaDTO.getNumeroCuenta());
                verify(usuarioRepository).findById(createCuentaBancariaDTO.getUsuarioId());
                verify(cuentaBancariaRepository).save(any(CuentaBancaria.class));
                verify(cuentaBancariaMapper).toDTO(cuentaBancaria);
        }

        @Test
        void crearCuenta_ConNumeroCuentaExistente_DeberiaLanzarExcepcion() {
                // Given
                when(cuentaBancariaRepository
                                .existsByNumeroCuenta(createCuentaBancariaDTO.getNumeroCuenta()))
                                                .thenReturn(true);

                // When
                NumeroCuentaAlreadyExistsException exception = assertThrows(
                                NumeroCuentaAlreadyExistsException.class,
                                () -> cuentaBancariaService.crearCuenta(createCuentaBancariaDTO));

                // Then
                assertAll("Validación de excepción por número de cuenta duplicado",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals("Ya existe una cuenta con el número: "
                                                + createCuentaBancariaDTO.getNumeroCuenta(),
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(cuentaBancariaRepository)
                                .existsByNumeroCuenta(createCuentaBancariaDTO.getNumeroCuenta());
                verify(usuarioRepository, never()).findById(any());
                verify(cuentaBancariaRepository, never()).save(any());
                verifyNoInteractions(cuentaBancariaMapper);
        }

        @Test
        void crearCuenta_ConUsuarioInexistente_DeberiaLanzarExcepcion() {
                // Given
                when(cuentaBancariaRepository
                                .existsByNumeroCuenta(createCuentaBancariaDTO.getNumeroCuenta()))
                                                .thenReturn(false);
                when(usuarioRepository.findById(createCuentaBancariaDTO.getUsuarioId()))
                                .thenReturn(Optional.empty());

                // When
                UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class,
                                () -> cuentaBancariaService.crearCuenta(createCuentaBancariaDTO));

                // Then
                assertAll("Validación de excepción por usuario no encontrado",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals(
                                                "Usuario con ID "
                                                                + createCuentaBancariaDTO
                                                                                .getUsuarioId()
                                                                + " no encontrado",
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(cuentaBancariaRepository)
                                .existsByNumeroCuenta(createCuentaBancariaDTO.getNumeroCuenta());
                verify(usuarioRepository).findById(createCuentaBancariaDTO.getUsuarioId());
                verify(cuentaBancariaRepository, never()).save(any());
                verifyNoInteractions(cuentaBancariaMapper);
        }

        @Test
        void obtenerCuentasPorUsuario_ConCuentasExistentes_DeberiaRetornarLista() {
                // Given
                Long usuarioId = 1L;
                CuentaBancaria cuenta2 = CuentaBancaria.builder().id(2L).numeroCuenta("0987654321")
                                .saldoActual(new BigDecimal("2000.00")).usuario(usuario).build();

                CuentaBancariaDTO cuentaDTO2 =
                                CuentaBancariaDTO.builder().id(2L).numeroCuenta("0987654321")
                                                .saldoActual(new BigDecimal("2000.00")).build();

                List<CuentaBancaria> cuentas = Arrays.asList(cuentaBancaria, cuenta2);
                List<CuentaBancariaDTO> cuentasDTO = Arrays.asList(cuentaBancariaDTO, cuentaDTO2);

                when(cuentaBancariaRepository.findByUsuarioId(usuarioId)).thenReturn(cuentas);
                when(cuentaBancariaMapper.toDTOList(cuentas)).thenReturn(cuentasDTO);

                // When
                List<CuentaBancariaDTO> resultado =
                                cuentaBancariaService.obtenerCuentasPorUsuario(usuarioId);

                // Then
                assertAll("Lista de cuentas obtenida exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(2, resultado.size(), "Debe retornar 2 cuentas"),
                                () -> assertEquals(cuentaBancariaDTO.getId(),
                                                resultado.get(0).getId(),
                                                "La primera cuenta debe coincidir"),
                                () -> assertEquals(cuentaDTO2.getId(), resultado.get(1).getId(),
                                                "La segunda cuenta debe coincidir"));

                verify(cuentaBancariaRepository).findByUsuarioId(usuarioId);
                verify(cuentaBancariaMapper).toDTOList(cuentas);
                verifyNoMoreInteractions(cuentaBancariaRepository, cuentaBancariaMapper);
        }

        @Test
        void obtenerCuentasPorUsuario_SinCuentas_DeberiaRetornarListaVacia() {
                // Given
                Long usuarioId = 999L;
                List<CuentaBancaria> cuentas = Arrays.asList();
                List<CuentaBancariaDTO> cuentasDTO = Arrays.asList();

                when(cuentaBancariaRepository.findByUsuarioId(usuarioId)).thenReturn(cuentas);
                when(cuentaBancariaMapper.toDTOList(cuentas)).thenReturn(cuentasDTO);

                // When
                List<CuentaBancariaDTO> resultado =
                                cuentaBancariaService.obtenerCuentasPorUsuario(usuarioId);

                // Then
                assertAll("Lista vacía retornada correctamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertTrue(resultado.isEmpty(), "La lista debe estar vacía"));

                verify(cuentaBancariaRepository).findByUsuarioId(usuarioId);
                verify(cuentaBancariaMapper).toDTOList(cuentas);
                verifyNoMoreInteractions(cuentaBancariaRepository, cuentaBancariaMapper);
        }

        @Test
        void eliminarCuenta_ConCuentaExistente_DeberiaEliminarCuenta() {
                // Given
                Long cuentaId = 1L;
                when(cuentaBancariaRepository.existsById(cuentaId)).thenReturn(true);

                // When
                String resultado = cuentaBancariaService.eliminarCuenta(cuentaId);

                // Then
                assertAll("Cuenta eliminada exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals("Cuenta eliminada exitosamente", resultado,
                                                "El mensaje debe ser correcto"));

                verify(cuentaBancariaRepository).existsById(cuentaId);
                verify(cuentaBancariaRepository).deleteById(cuentaId);
                verifyNoMoreInteractions(cuentaBancariaRepository);
                verifyNoInteractions(cuentaBancariaMapper);
        }

        @Test
        void eliminarCuenta_ConCuentaInexistente_DeberiaLanzarExcepcion() {
                // Given
                Long cuentaId = 999L;
                when(cuentaBancariaRepository.existsById(cuentaId)).thenReturn(false);

                // When
                CuentaBancariaNotFoundException exception = assertThrows(
                                CuentaBancariaNotFoundException.class,
                                () -> cuentaBancariaService.eliminarCuenta(cuentaId));

                // Then
                assertAll("Validación de excepción por cuenta no encontrada en eliminación",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals(
                                                "Cuenta bancaria con ID " + cuentaId
                                                                + " no encontrada",
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(cuentaBancariaRepository).existsById(cuentaId);
                verify(cuentaBancariaRepository, never()).deleteById(any());
                verifyNoMoreInteractions(cuentaBancariaRepository);
                verifyNoInteractions(cuentaBancariaMapper);
        }
}
