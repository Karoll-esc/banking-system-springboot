package com.sofka.banking.system.dto.request;

import java.math.BigDecimal;
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
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "El número de cuenta debe tener entre 10 y 20 dígitos")
    private String numeroCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial no puede ser negativo")
    private BigDecimal saldoActual;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long usuarioId;
}
