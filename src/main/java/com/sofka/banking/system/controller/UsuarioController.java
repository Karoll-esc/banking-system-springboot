package com.sofka.banking.system.controller;

import java.util.List;
import java.util.Map;

import com.sofka.banking.system.service.UsuarioService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.request.UpdateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService; // Usa la interfaz, no la implementación

    @Operation(summary = "Obtener todos los usuarios",
            description = "Devuelve una lista de todos los usuarios registrados.")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Obtener usuario por ID",
            description = "Devuelve la información de un usuario específico por su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Crear un nuevo usuario",
            description = "Crea un usuario con los datos proporcionados.")
    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody CreateUsuarioDTO crearUsuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(crearUsuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @Operation(summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente por su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUsuarioDTO datosActualizados) {
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, datosActualizados);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @Operation(summary = "Eliminar usuario",
            description = "Elimina un usuario por su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable Long id) {
        String mensaje = usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(Map.of("message", mensaje));
    }
}
