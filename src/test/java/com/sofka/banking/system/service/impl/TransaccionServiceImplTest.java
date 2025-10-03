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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.request.CreateTransferenciaDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Transaccion;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.enums.TipoTransaccion;
import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.exception.transaccion.MontoInvalidoException;
import com.sofka.banking.system.exception.transaccion.SaldoInsuficienteException;
import com.sofka.banking.system.mapper.TransaccionMapper;
import com.sofka.banking.system.repository.CuentaBancariaRepository;
import com.sofka.banking.system.repository.TransaccionRepository;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceImplTest {

        @Mock
        private TransaccionRepository transaccionRepository;

        @Mock
        private CuentaBancariaRepository cuentaBancariaRepository;

        @Mock
        private TransaccionMapper transaccionMapper;

        @InjectMocks
        private TransaccionServiceImpl transaccionService;

        private Usuario usuario;
        private CuentaBancaria cuentaBancaria;
        private CuentaBancaria cuentaDestino;
        private Transaccion transaccion;
        private TransaccionDTO transaccionDTO;
        private CreateTransaccionDTO createTransaccionDTO;
        private CreateTransferenciaDTO createTransferenciaDTO;

        @BeforeEach
        void setUp() {
                usuario = Usuario.builder().id(1L).cedula("12345678").nombre("Juan")
                                .apellido("Pérez").email("juan@email.com").telefono("1234567890")
                                .build();

                cuentaBancaria = CuentaBancaria.builder().id(1L).numeroCuenta("1234567890")
                                .saldoActual(new BigDecimal("1000.00")).usuario(usuario).build();

                cuentaDestino = CuentaBancaria.builder().id(2L).numeroCuenta("0987654321")
                                .saldoActual(new BigDecimal("500.00")).usuario(usuario).build();

                transaccion = Transaccion.builder().id(1L).monto(new BigDecimal("500.00"))
                                .tipo(TipoTransaccion.DEPOSITO).fecha(LocalDateTime.now())
                                .cuentaBancaria(cuentaBancaria).build();

                transaccionDTO = TransaccionDTO.builder().id(1L).monto(new BigDecimal("500.00"))
                                .tipo(TipoTransaccion.DEPOSITO).fecha(LocalDateTime.now())
                                .cuentaBancariaId(1L).build();

                createTransaccionDTO = CreateTransaccionDTO.builder().cuentaBancariaId(1L)
                                .monto(new BigDecimal("500.00")).tipo(TipoTransaccion.DEPOSITO)
                                .build();

                createTransferenciaDTO = CreateTransferenciaDTO.builder().cuentaOrigenId(1L)
                                .cuentaDestinoId(2L).monto(new BigDecimal("200.00")).build();
        }

        @Test
        void registrarTransaccion_DepositoValido_DeberiaRegistrarTransaccion() {
                // Given
                when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));
                when(cuentaBancariaRepository.save(any(CuentaBancaria.class)))
                                .thenReturn(cuentaBancaria);
                when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);
                when(transaccionMapper.toDTO(transaccion)).thenReturn(transaccionDTO);

                // When
                TransaccionDTO resultado =
                                transaccionService.registrarTransaccion(createTransaccionDTO);

                // Then
                assertNotNull(resultado);
                assertEquals(transaccionDTO.getMonto(), resultado.getMonto());
                assertEquals(transaccionDTO.getTipo(), resultado.getTipo());
                verify(cuentaBancariaRepository).findById(1L);
                verify(cuentaBancariaRepository).save(any(CuentaBancaria.class));
                verify(transaccionRepository).save(any(Transaccion.class));
        }

        @Test
        void registrarTransaccion_ConCuentaInexistente_DeberiaLanzarExcepcion() {
                // Given
                when(cuentaBancariaRepository.findById(999L)).thenReturn(Optional.empty());

                CreateTransaccionDTO dto = CreateTransaccionDTO.builder().cuentaBancariaId(999L)
                                .monto(new BigDecimal("500.00")).tipo(TipoTransaccion.DEPOSITO)
                                .build();

                // When & Then
                CuentaBancariaNotFoundException exception =
                                assertThrows(CuentaBancariaNotFoundException.class,
                                                () -> transaccionService.registrarTransaccion(dto));

                assertEquals("Cuenta bancaria con ID 999 no encontrada", exception.getMessage());
                verify(cuentaBancariaRepository).findById(999L);
                verify(transaccionRepository, never()).save(any());
        }

        @Test
        void registrarTransaccion_RetiroConSaldoInsuficiente_DeberiaLanzarExcepcion() {
                // Given
                CreateTransaccionDTO retiroDTO = CreateTransaccionDTO.builder().cuentaBancariaId(1L)
                                .monto(new BigDecimal("1500.00")) // Más del saldo disponible
                                .tipo(TipoTransaccion.RETIRO).build();

                when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));

                // When & Then
                SaldoInsuficienteException exception = assertThrows(
                                SaldoInsuficienteException.class,
                                () -> transaccionService.registrarTransaccion(retiroDTO));

                assertTrue(exception.getMessage().contains("Saldo insuficiente"));
                verify(cuentaBancariaRepository).findById(1L);
                verify(cuentaBancariaRepository, never()).save(any());
                verify(transaccionRepository, never()).save(any());
        }

        @Test
        void registrarTransaccion_RetiroValido_DeberiaRegistrarTransaccion() {
                // Given
                CreateTransaccionDTO retiroDTO = CreateTransaccionDTO.builder().cuentaBancariaId(1L)
                                .monto(new BigDecimal("300.00")) // Menos del saldo disponible
                                .tipo(TipoTransaccion.RETIRO).build();

                Transaccion transaccionRetiro = Transaccion.builder().id(2L)
                                .monto(new BigDecimal("300.00")).tipo(TipoTransaccion.RETIRO)
                                .fecha(LocalDateTime.now()).cuentaBancaria(cuentaBancaria).build();

                TransaccionDTO transaccionRetiroDTO = TransaccionDTO.builder().id(2L)
                                .monto(new BigDecimal("300.00")).tipo(TipoTransaccion.RETIRO)
                                .fecha(LocalDateTime.now()).cuentaBancariaId(1L).build();

                when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));
                when(cuentaBancariaRepository.save(any(CuentaBancaria.class)))
                                .thenReturn(cuentaBancaria);
                when(transaccionRepository.save(any(Transaccion.class)))
                                .thenReturn(transaccionRetiro);
                when(transaccionMapper.toDTO(transaccionRetiro)).thenReturn(transaccionRetiroDTO);

                // When
                TransaccionDTO resultado = transaccionService.registrarTransaccion(retiroDTO);

                // Then
                assertAll("Retiro registrado exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(transaccionRetiroDTO.getMonto(),
                                                resultado.getMonto(), "El monto debe coincidir"),
                                () -> assertEquals(TipoTransaccion.RETIRO, resultado.getTipo(),
                                                "El tipo debe ser RETIRO"),
                                () -> assertEquals(transaccionRetiroDTO.getCuentaBancariaId(),
                                                resultado.getCuentaBancariaId(),
                                                "La cuenta debe coincidir"));

                verify(cuentaBancariaRepository).findById(1L);
                verify(cuentaBancariaRepository).save(any(CuentaBancaria.class));
                verify(transaccionRepository).save(any(Transaccion.class));
                verify(transaccionMapper).toDTO(transaccionRetiro);
        }

        @Test
        void realizarTransferencia_ConDatosValidos_DeberiaRealizarTransferencia() {
                // Given
                Transaccion transferenciaTransaccion = Transaccion.builder().id(3L)
                                .monto(new BigDecimal("200.00")).tipo(TipoTransaccion.TRANSFERENCIA)
                                .fecha(LocalDateTime.now()).cuentaBancaria(cuentaBancaria)
                                .cuentaDestino(cuentaDestino).build();

                TransaccionDTO transferenciaDTO = TransaccionDTO.builder().id(3L)
                                .monto(new BigDecimal("200.00")).tipo(TipoTransaccion.TRANSFERENCIA)
                                .fecha(LocalDateTime.now()).cuentaBancariaId(1L).cuentaDestinoId(2L)
                                .build();

                when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));
                when(cuentaBancariaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));
                when(cuentaBancariaRepository.save(cuentaBancaria)).thenReturn(cuentaBancaria);
                when(cuentaBancariaRepository.save(cuentaDestino)).thenReturn(cuentaDestino);
                when(transaccionRepository.save(any(Transaccion.class)))
                                .thenReturn(transferenciaTransaccion);
                when(transaccionMapper.toDTO(transferenciaTransaccion))
                                .thenReturn(transferenciaDTO);

                // When
                TransaccionDTO resultado =
                                transaccionService.realizarTransferencia(createTransferenciaDTO);

                // Then
                assertAll("Transferencia realizada exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(transferenciaDTO.getMonto(),
                                                resultado.getMonto(), "El monto debe coincidir"),
                                () -> assertEquals(TipoTransaccion.TRANSFERENCIA,
                                                resultado.getTipo(),
                                                "El tipo debe ser TRANSFERENCIA"),
                                () -> assertEquals(transferenciaDTO.getCuentaBancariaId(),
                                                resultado.getCuentaBancariaId(),
                                                "La cuenta origen debe coincidir"),
                                () -> assertEquals(transferenciaDTO.getCuentaDestinoId(),
                                                resultado.getCuentaDestinoId(),
                                                "La cuenta destino debe coincidir"));

                verify(cuentaBancariaRepository).findById(1L);
                verify(cuentaBancariaRepository).findById(2L);
                verify(cuentaBancariaRepository).save(cuentaBancaria);
                verify(cuentaBancariaRepository).save(cuentaDestino);
                verify(transaccionRepository).save(any(Transaccion.class));
                verify(transaccionMapper).toDTO(transferenciaTransaccion);
        }

        @Test
        void realizarTransferencia_ConCuentaOrigenInexistente_DeberiaLanzarExcepcion() {
                // Given
                CreateTransferenciaDTO transferenciaDTOInvalido = CreateTransferenciaDTO.builder()
                                .cuentaOrigenId(999L).cuentaDestinoId(2L)
                                .monto(new BigDecimal("200.00")).build();

                when(cuentaBancariaRepository.findById(999L)).thenReturn(Optional.empty());

                // When
                CuentaBancariaNotFoundException exception = assertThrows(
                                CuentaBancariaNotFoundException.class, () -> transaccionService
                                                .realizarTransferencia(transferenciaDTOInvalido));

                // Then
                assertAll("Validación de excepción por cuenta origen no encontrada",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals("Cuenta bancaria con ID 999 no encontrada",
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(cuentaBancariaRepository).findById(999L);
                verify(cuentaBancariaRepository, never()).findById(2L);
                verify(cuentaBancariaRepository, never()).save(any());
                verify(transaccionRepository, never()).save(any());
                verifyNoInteractions(transaccionMapper);
        }

        @Test
        void realizarTransferencia_ConCuentaDestinoInexistente_DeberiaLanzarExcepcion() {
                // Given
                CreateTransferenciaDTO transferenciaDTOInvalido = CreateTransferenciaDTO.builder()
                                .cuentaOrigenId(1L).cuentaDestinoId(999L)
                                .monto(new BigDecimal("200.00")).build();

                when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));
                when(cuentaBancariaRepository.findById(999L)).thenReturn(Optional.empty());

                // When
                CuentaBancariaNotFoundException exception = assertThrows(
                                CuentaBancariaNotFoundException.class, () -> transaccionService
                                                .realizarTransferencia(transferenciaDTOInvalido));

                // Then
                assertAll("Validación de excepción por cuenta destino no encontrada",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertEquals("Cuenta bancaria con ID 999 no encontrada",
                                                exception.getMessage(),
                                                "El mensaje de error debe ser correcto"));

                verify(cuentaBancariaRepository).findById(1L);
                verify(cuentaBancariaRepository).findById(999L);
                verify(cuentaBancariaRepository, never()).save(any());
                verify(transaccionRepository, never()).save(any());
                verifyNoInteractions(transaccionMapper);
        }

        @Test
        void realizarTransferencia_ConSaldoInsuficiente_DeberiaLanzarExcepcion() {
                // Given
                CreateTransferenciaDTO transferenciaDTOInvalido = CreateTransferenciaDTO.builder()
                                .cuentaOrigenId(1L).cuentaDestinoId(2L)
                                .monto(new BigDecimal("1500.00")) // Más del saldo disponible
                                .build();

                when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));
                when(cuentaBancariaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));

                // When
                SaldoInsuficienteException exception = assertThrows(
                                SaldoInsuficienteException.class, () -> transaccionService
                                                .realizarTransferencia(transferenciaDTOInvalido));

                // Then
                assertAll("Validación de excepción por saldo insuficiente en transferencia",
                                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                                () -> assertTrue(
                                                exception.getMessage()
                                                                .contains("Saldo insuficiente"),
                                                "El mensaje debe indicar saldo insuficiente"));

                verify(cuentaBancariaRepository).findById(1L);
                verify(cuentaBancariaRepository).findById(2L);
                verify(cuentaBancariaRepository, never()).save(any());
                verify(transaccionRepository, never()).save(any());
                verifyNoInteractions(transaccionMapper);
        }

        @Test
        void obtenerTransaccionesPorCuenta_ConTransaccionesExistentes_DeberiaRetornarLista() {
                // Given
                Long cuentaId = 1L;
                Transaccion transaccion2 = Transaccion.builder().id(2L)
                                .monto(new BigDecimal("300.00")).tipo(TipoTransaccion.RETIRO)
                                .fecha(LocalDateTime.now()).cuentaBancaria(cuentaBancaria).build();

                TransaccionDTO transaccionDTO2 = TransaccionDTO.builder().id(2L)
                                .monto(new BigDecimal("300.00")).tipo(TipoTransaccion.RETIRO)
                                .fecha(LocalDateTime.now()).cuentaBancariaId(1L).build();

                List<Transaccion> transacciones = Arrays.asList(transaccion, transaccion2);
                List<TransaccionDTO> transaccionesDTO =
                                Arrays.asList(transaccionDTO, transaccionDTO2);

                when(transaccionRepository.findByCuentaBancariaId(cuentaId))
                                .thenReturn(transacciones);
                when(transaccionMapper.toDTOList(transacciones)).thenReturn(transaccionesDTO);

                // When
                List<TransaccionDTO> resultado =
                                transaccionService.obtenerTransaccionesPorCuenta(cuentaId);

                // Then
                assertAll("Lista de transacciones obtenida exitosamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertEquals(2, resultado.size(),
                                                "Debe retornar 2 transacciones"),
                                () -> assertEquals(transaccionDTO.getId(), resultado.get(0).getId(),
                                                "La primera transacción debe coincidir"),
                                () -> assertEquals(transaccionDTO2.getId(),
                                                resultado.get(1).getId(),
                                                "La segunda transacción debe coincidir"));

                verify(transaccionRepository).findByCuentaBancariaId(cuentaId);
                verify(transaccionMapper).toDTOList(transacciones);
                verifyNoMoreInteractions(transaccionRepository, transaccionMapper);
        }

        @Test
        void obtenerTransaccionesPorCuenta_SinTransacciones_DeberiaRetornarListaVacia() {
                // Given
                Long cuentaId = 999L;
                List<Transaccion> transacciones = Arrays.asList();
                List<TransaccionDTO> transaccionesDTO = Arrays.asList();

                when(transaccionRepository.findByCuentaBancariaId(cuentaId))
                                .thenReturn(transacciones);
                when(transaccionMapper.toDTOList(transacciones)).thenReturn(transaccionesDTO);

                // When
                List<TransaccionDTO> resultado =
                                transaccionService.obtenerTransaccionesPorCuenta(cuentaId);

                // Then
                assertAll("Lista vacía retornada correctamente",
                                () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
                                () -> assertTrue(resultado.isEmpty(), "La lista debe estar vacía"));

                verify(transaccionRepository).findByCuentaBancariaId(cuentaId);
                verify(transaccionMapper).toDTOList(transacciones);
                verifyNoMoreInteractions(transaccionRepository, transaccionMapper);
        }
}
