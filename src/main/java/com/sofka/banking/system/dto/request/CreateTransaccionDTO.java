package com.sofka.banking.system.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransaccionDTO {
    private Long cuentaBancariaId;
    private BigDecimal monto;
    private String tipo;
}
