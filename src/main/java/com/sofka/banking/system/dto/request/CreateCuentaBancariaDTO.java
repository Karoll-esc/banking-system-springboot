package com.sofka.banking.system.dto.request;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCuentaBancariaDTO {
    @Schema(description = "Número único de la cuenta bancaria", example = "1234567890123456",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Pattern(regexp = "^[0-9]{10,20}$",
            message = "El número de cuenta debe tener entre 10 y 20 dígitos")
    private String numeroCuenta;

    @Schema(description = "Saldo inicial de la cuenta bancaria", example = "1000.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true,
            message = "El saldo inicial no puede ser negativo")
    private BigDecimal saldoActual;

    @Schema(description = "ID del usuario propietario de la cuenta", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long usuarioId;
}
