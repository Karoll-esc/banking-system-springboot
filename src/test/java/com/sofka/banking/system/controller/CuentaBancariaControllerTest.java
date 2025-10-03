package com.sofka.banking.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import com.sofka.banking.system.exception.GlobalExceptionHandler;
import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.service.CuentaBancariaService;

@ExtendWith(MockitoExtension.class)
class CuentaBancariaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CuentaBancariaService cuentaBancariaService;

    @InjectMocks
    private CuentaBancariaController cuentaBancariaController;

    private ObjectMapper objectMapper;

    private CreateCuentaBancariaDTO createCuentaBancariaDTO;
    private CuentaBancariaDTO cuentaBancariaDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(cuentaBancariaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        createCuentaBancariaDTO = CreateCuentaBancariaDTO.builder()
                .numeroCuenta("1234567890123456")
                .saldoActual(new BigDecimal("1000.00"))
                .usuarioId(1L)
                .build();

        cuentaBancariaDTO = CuentaBancariaDTO.builder()
                .id(1L)
                .numeroCuenta("1234567890123456")
                .saldoActual(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void crearCuenta_ConDatosValidos_DeberiaRetornar201YCuentaCreada() throws Exception {
        // Given
        when(cuentaBancariaService.crearCuenta(any(CreateCuentaBancariaDTO.class)))
                .thenReturn(cuentaBancariaDTO);

        // When & Then
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCuentaBancariaDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numeroCuenta").value("1234567890123456"))
                .andExpect(jsonPath("$.saldoActual").value(1000.00));

        // Verify - Verificar que el servicio fue llamado con los datos correctos
        verify(cuentaBancariaService).crearCuenta(any(CreateCuentaBancariaDTO.class));
    }


    @Test
    void consultarSaldo_ConCuentaInexistente_DeberiaRetornar404() throws Exception {
        // Given
        Long cuentaIdInexistente = 999L;
        when(cuentaBancariaService.consultarSaldo(cuentaIdInexistente))
                .thenThrow(new CuentaBancariaNotFoundException(cuentaIdInexistente));

        // When & Then
        mockMvc.perform(get("/cuentas/{cuentaId}/saldo", cuentaIdInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cuenta bancaria con ID 999 no encontrada"));

        // Verify - Verificar que el servicio fue llamado con el ID correcto
        verify(cuentaBancariaService).consultarSaldo(cuentaIdInexistente);
    }

}