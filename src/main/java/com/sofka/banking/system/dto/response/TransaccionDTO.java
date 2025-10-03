package com.sofka.banking.system.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sofka.banking.system.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionDTO {
    private Long id;
    private BigDecimal monto;
    private TipoTransaccion tipo;
    private LocalDateTime fecha;
    private Long cuentaBancariaId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long cuentaDestinoId; // Para transferencias
}
