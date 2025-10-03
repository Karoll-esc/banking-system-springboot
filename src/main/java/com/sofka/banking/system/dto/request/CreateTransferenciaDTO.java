package com.sofka.banking.system.dto.request;

import java.math.BigDecimal;
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
public class CreateTransferenciaDTO {

    @Schema(description = "ID de la cuenta bancaria origen de la transferencia", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID de la cuenta origen es obligatorio")
    @Positive(message = "El ID de la cuenta origen debe ser un número positivo")
    private Long cuentaOrigenId;

    @Schema(description = "ID de la cuenta bancaria destino de la transferencia", example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID de la cuenta destino es obligatorio")
    @Positive(message = "El ID de la cuenta destino debe ser un número positivo")
    private Long cuentaDestinoId;

    @Schema(description = "Monto a transferir entre las cuentas", example = "250.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;
}
