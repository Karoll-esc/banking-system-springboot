package com.sofka.banking.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.enums.TipoTransaccion;
import com.sofka.banking.system.exception.GlobalExceptionHandler;
import com.sofka.banking.system.exception.transaccion.SaldoInsuficienteException;
import com.sofka.banking.system.service.TransaccionService;

@ExtendWith(MockitoExtension.class)
class TransaccionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransaccionService transaccionService;

    @InjectMocks
    private TransaccionController transaccionController;

    private ObjectMapper objectMapper;

    private CreateTransaccionDTO createTransaccionDTO;
    private TransaccionDTO transaccionDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(transaccionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        createTransaccionDTO = CreateTransaccionDTO.builder()
                .cuentaBancariaId(1L)
                .monto(new BigDecimal("500.00"))
                .tipo(TipoTransaccion.DEPOSITO)
                .build();

        transaccionDTO = TransaccionDTO.builder()
                .id(1L)
                .cuentaBancariaId(1L)
                .monto(new BigDecimal("500.00"))
                .tipo(TipoTransaccion.DEPOSITO)
                .fecha(LocalDateTime.now())
                .build();
    }

    @Test
    void registrarTransaccion_ConDatosValidos_DeberiaRetornar201YTransaccionCreada() throws Exception {
        // Given
        when(transaccionService.registrarTransaccion(any(CreateTransaccionDTO.class)))
                .thenReturn(transaccionDTO);

        // When & Then
        mockMvc.perform(post("/transacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTransaccionDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cuentaBancariaId").value(1L))
                .andExpect(jsonPath("$.monto").value(500.00))
                .andExpect(jsonPath("$.tipo").value("DEPOSITO"));

        // Verify - Verificar que el servicio fue llamado con los datos correctos
        verify(transaccionService).registrarTransaccion(any(CreateTransaccionDTO.class));
    }

    @Test
    void registrarTransaccion_ConSaldoInsuficiente_DeberiaRetornar400() throws Exception {
        // Given
        CreateTransaccionDTO retiroDTO = CreateTransaccionDTO.builder()
                .cuentaBancariaId(1L)
                .monto(new BigDecimal("1000.00"))
                .tipo(TipoTransaccion.RETIRO)
                .build();

        when(transaccionService.registrarTransaccion(any(CreateTransaccionDTO.class)))
                .thenThrow(new SaldoInsuficienteException(new BigDecimal("100.00"), new BigDecimal("1000.00")));

        // When & Then
        mockMvc.perform(post("/transacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(retiroDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Saldo insuficiente. Saldo actual: 100.00, Monto requerido: 1000.00"));

        // Verify - Verificar que el servicio fue llamado con los datos correctos
        verify(transaccionService).registrarTransaccion(any(CreateTransaccionDTO.class));
    }
}