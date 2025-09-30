package com.sofka.banking.system.exception.transaccion;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(BigDecimal saldoActual, BigDecimal montoRequerido) {
        super(String.format("Saldo insuficiente. Saldo actual: %s, Monto requerido: %s",
                saldoActual, montoRequerido));
    }
}