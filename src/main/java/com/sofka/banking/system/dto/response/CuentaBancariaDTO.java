package com.sofka.banking.system.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaBancariaDTO {
    private Long id;
    private String numeroCuenta;
    private BigDecimal saldoActual;
}
