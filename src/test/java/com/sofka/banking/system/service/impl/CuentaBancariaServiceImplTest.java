package com.sofka.banking.system.service.impl;

import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.mapper.CuentaBancariaMapper;
import com.sofka.banking.system.repository.CuentaBancariaRepository;
import com.sofka.banking.system.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        cuentaBancariaDTO = CuentaBancariaDTO.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldoActual(new BigDecimal("1000.00"))
                .build();

        createCuentaBancariaDTO = CreateCuentaBancariaDTO.builder()
                .numeroCuenta("1234567890")
                .saldoActual(new BigDecimal("1000.00"))
                .usuarioId(1L)
                .build();
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
        CuentaBancariaNotFoundException exception = assertThrows(
                CuentaBancariaNotFoundException.class,
                () -> cuentaBancariaService.consultarSaldo(999L)
        );

        assertEquals("Cuenta bancaria con ID 999 no encontrada", exception.getMessage());
        verify(cuentaBancariaRepository).findById(999L);
        verify(cuentaBancariaMapper, never()).toDTO(any());
    }
}
