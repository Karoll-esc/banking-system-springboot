package com.sofka.banking.system.dto.request;

import java.math.BigDecimal;
import com.sofka.banking.system.enums.TipoTransaccion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransaccionDTO {

    @Schema(description = "ID de la cuenta bancaria donde se realizará la transacción",
            example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID de la cuenta bancaria es obligatorio")
    @Positive(message = "El ID de la cuenta debe ser un número positivo")
    private Long cuentaBancariaId;

    @Schema(description = "Monto de la transacción", example = "500.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Schema(description = "Tipo de transacción", example = "DEPOSITO",
            allowableValues = {"DEPOSITO", "RETIRO"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El tipo de transacción es obligatorio")
    private TipoTransaccion tipo;
}
