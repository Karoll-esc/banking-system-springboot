package com.sofka.banking.system.service.impl;

import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Transaccion;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.enums.TipoTransaccion;
import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.exception.transaccion.SaldoInsuficienteException;
import com.sofka.banking.system.mapper.TransaccionMapper;
import com.sofka.banking.system.repository.CuentaBancariaRepository;
import com.sofka.banking.system.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private Transaccion transaccion;
    private TransaccionDTO transaccionDTO;
    private CreateTransaccionDTO createTransaccionDTO;

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

        cuentaBancaria = CuentaBancaria.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldoActual(new BigDecimal("1000.00"))
                .usuario(usuario)
                .build();

        transaccion = Transaccion.builder()
                .id(1L)
                .monto(new BigDecimal("500.00"))
                .tipo(TipoTransaccion.DEPOSITO)
                .fecha(LocalDateTime.now())
                .cuentaBancaria(cuentaBancaria)
                .build();

        transaccionDTO = TransaccionDTO.builder()
                .id(1L)
                .monto(new BigDecimal("500.00"))
                .tipo(TipoTransaccion.DEPOSITO)
                .fecha(LocalDateTime.now())
                .cuentaBancariaId(1L)
                .build();

        createTransaccionDTO = CreateTransaccionDTO.builder()
                .cuentaBancariaId(1L)
                .monto(new BigDecimal("500.00"))
                .tipo(TipoTransaccion.DEPOSITO)
                .build();
    }

    @Test
    void registrarTransaccion_DepositoValido_DeberiaRegistrarTransaccion() {
        // Given
        when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));
        when(cuentaBancariaRepository.save(any(CuentaBancaria.class))).thenReturn(cuentaBancaria);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);
        when(transaccionMapper.toDTO(transaccion)).thenReturn(transaccionDTO);

        // When
        TransaccionDTO resultado = transaccionService.registrarTransaccion(createTransaccionDTO);

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

        CreateTransaccionDTO dto = CreateTransaccionDTO.builder()
                .cuentaBancariaId(999L)
                .monto(new BigDecimal("500.00"))
                .tipo(TipoTransaccion.DEPOSITO)
                .build();

        // When & Then
        CuentaBancariaNotFoundException exception = assertThrows(
                CuentaBancariaNotFoundException.class,
                () -> transaccionService.registrarTransaccion(dto)
        );

        assertEquals("Cuenta bancaria con ID 999 no encontrada", exception.getMessage());
        verify(cuentaBancariaRepository).findById(999L);
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void registrarTransaccion_RetiroConSaldoInsuficiente_DeberiaLanzarExcepcion() {
        // Given
        CreateTransaccionDTO retiroDTO = CreateTransaccionDTO.builder()
                .cuentaBancariaId(1L)
                .monto(new BigDecimal("1500.00")) // Más del saldo disponible
                .tipo(TipoTransaccion.RETIRO)
                .build();

        when(cuentaBancariaRepository.findById(1L)).thenReturn(Optional.of(cuentaBancaria));

        // When & Then
        SaldoInsuficienteException exception = assertThrows(
                SaldoInsuficienteException.class,
                () -> transaccionService.registrarTransaccion(retiroDTO)
        );

        assertTrue(exception.getMessage().contains("Saldo insuficiente"));
        verify(cuentaBancariaRepository).findById(1L);
        verify(cuentaBancariaRepository, never()).save(any());
        verify(transaccionRepository, never()).save(any());
    }
}
