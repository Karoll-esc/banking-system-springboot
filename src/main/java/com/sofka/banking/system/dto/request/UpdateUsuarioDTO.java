package com.sofka.banking.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUsuarioDTO {
    @Schema(description = "Nombre del usuario (opcional para actualización)",
            example = "Juan Carlos")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$",
            message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @Schema(description = "Apellido del usuario (opcional para actualización)",
            example = "Pérez González")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$",
            message = "El apellido solo puede contener letras y espacios")
    private String apellido;

    @Schema(description = "Correo electrónico del usuario (opcional para actualización)",
            example = "juan.carlos@email.com")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Schema(description = "Número de teléfono del usuario (opcional para actualización)",
            example = "+573009876543")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$",
            message = "El teléfono debe tener entre 10 y 15 dígitos, opcionalmente con +")
    private String telefono;
}
