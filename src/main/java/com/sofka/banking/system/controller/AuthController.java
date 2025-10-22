package com.sofka.banking.system.controller;

import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.request.LoginRequestDTO;
import com.sofka.banking.system.dto.response.LoginResponseDTO;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@Tag(name = "Autenticación", description = "Endpoints de autenticación de usuarios")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Login de usuario",
            description = "Valida las credenciales del usuario (cédula y contraseña). "
                    + "Retorna los datos del usuario si las credenciales son correctas.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")})
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {

        // Buscar usuario por cédula
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCedula(loginRequest.getCedula());

        // Si el usuario no existe o la contraseña no coincide
        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(),
                usuarioOpt.get().getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }

        // Credenciales correctas - construir respuesta
        Usuario usuario = usuarioOpt.get();

        LoginResponseDTO response =
                LoginResponseDTO.builder().id(usuario.getId()).cedula(usuario.getCedula())
                        .nombre(usuario.getNombre()).apellido(usuario.getApellido())
                        .email(usuario.getEmail()).telefono(usuario.getTelefono()).build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema con las credenciales proporcionadas. "
                    + "El password se hashea automáticamente con BCrypt antes de guardarse.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Email o cédula ya registrados"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")})
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreateUsuarioDTO registerRequest) {

        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "El email ya está registrado"));
        }

        // Validar que la cédula no exista
        if (usuarioRepository.existsByCedula(registerRequest.getCedula())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "La cédula ya está registrada"));
        }

        // Crear nuevo usuario
        Usuario nuevoUsuario = Usuario.builder().cedula(registerRequest.getCedula())
                .nombre(registerRequest.getNombre()).apellido(registerRequest.getApellido())
                .email(registerRequest.getEmail()).telefono(registerRequest.getTelefono())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // Hashear password
                .build();

        // Guardar en base de datos
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // Construir respuesta
        LoginResponseDTO response = LoginResponseDTO.builder().id(usuarioGuardado.getId())
                .cedula(usuarioGuardado.getCedula()).nombre(usuarioGuardado.getNombre())
                .apellido(usuarioGuardado.getApellido()).email(usuarioGuardado.getEmail())
                .telefono(usuarioGuardado.getTelefono()).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
