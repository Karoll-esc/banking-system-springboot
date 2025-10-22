package com.sofka.banking.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @Schema(description = "Número de cédula del usuario", example = "12345678",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "^[0-9]{8,10}$", message = "La cédula debe tener entre 8 y 10 dígitos")
    private String cedula;

    @Schema(description = "Contraseña del usuario", example = "miPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
