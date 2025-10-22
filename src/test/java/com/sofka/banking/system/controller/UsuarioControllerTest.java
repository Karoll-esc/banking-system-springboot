package com.sofka.banking.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import com.sofka.banking.system.exception.GlobalExceptionHandler;
import com.sofka.banking.system.exception.usuario.UsuarioNotFoundException;
import com.sofka.banking.system.service.UsuarioService;

@ExtendWith(MockitoExtension.class) // ← MANTENER solo esta
// ← ELIMINADO @WebMvcTest
class UsuarioControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UsuarioService usuarioService;

        @InjectMocks
        private UsuarioController usuarioController;

        private ObjectMapper objectMapper;

        private CreateUsuarioDTO createUsuarioDTO;
        private UsuarioDTO usuarioDTO;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper(); // ← AGREGADO: inicialización manual
                mockMvc = MockMvcBuilders.standaloneSetup(usuarioController) // ← AGREGADO
                                .setControllerAdvice(new GlobalExceptionHandler()) // ← AGREGADO
                                .build();

                createUsuarioDTO = CreateUsuarioDTO.builder().cedula("12345678")
                                .nombre("Juan Carlos").apellido("Pérez García")
                                .email("juan.perez@email.com").telefono("+573001234567")
                                .password("Password123!").build();

                usuarioDTO = UsuarioDTO.builder().id(1L).cedula("12345678").nombre("Juan Carlos")
                                .apellido("Pérez García").email("juan.perez@email.com")
                                .telefono("+573001234567").build();
        }

        @Test
        void crearUsuario_ConDatosValidos_DeberiaRetornar201YUsuarioCreado() throws Exception {
                // Given
                when(usuarioService.crearUsuario(any(CreateUsuarioDTO.class)))
                                .thenReturn(usuarioDTO);

                // When & Then
                mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createUsuarioDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.cedula").value("12345678"))
                                .andExpect(jsonPath("$.nombre").value("Juan Carlos"))
                                .andExpect(jsonPath("$.apellido").value("Pérez García"))
                                .andExpect(jsonPath("$.email").value("juan.perez@email.com"))
                                .andExpect(jsonPath("$.telefono").value("+573001234567"));

                verify(usuarioService).crearUsuario(any(CreateUsuarioDTO.class));
        }

        @Test
        void obtenerUsuarioPorId_ConIdInexistente_DeberiaRetornar404() throws Exception {
                // Given
                Long idInexistente = 999L;
                when(usuarioService.obtenerUsuarioPorId(idInexistente))
                                .thenThrow(new UsuarioNotFoundException(idInexistente));

                // When & Then
                mockMvc.perform(get("/usuarios/{id}", idInexistente)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message")
                                                .value("Usuario con ID 999 no encontrado"));

                verify(usuarioService).obtenerUsuarioPorId(idInexistente);
        }
}
