package com.sofka.banking.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CreateUsuarioDTO {

        @Schema(description = "Número de cédula de identidad del usuario", example = "12345678",
                        requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La cédula es obligatoria")
        @Pattern(regexp = "^[0-9]{8,10}$", message = "La cédula debe tener entre 8 y 10 dígitos")
        private String cedula;

        @Schema(description = "Nombre del usuario", example = "Juan",
                        requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$",
                        message = "El nombre solo puede contener letras y espacios")
        private String nombre;

        @Schema(description = "Apellido del usuario", example = "Pérez",
                        requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$",
                        message = "El apellido solo puede contener letras y espacios")
        private String apellido;

        @Schema(description = "Correo electrónico del usuario", example = "juan.perez@email.com",
                        requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 100, message = "El email no puede exceder 100 caracteres")
        private String email;

        @Schema(description = "Número de teléfono del usuario", example = "+573001234567",
                        requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[+]?[0-9]{10,15}$",
                        message = "El teléfono debe tener entre 10 y 15 dígitos, opcionalmente con +")
        private String telefono;

        @Schema(description = "Contraseña del usuario (será hasheada con BCrypt)",
                        example = "MiPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        private String password;
}
