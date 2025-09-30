package com.sofka.banking.system.exception.transaccion;

import java.math.BigDecimal;

public class MontoInvalidoException extends RuntimeException {
    public MontoInvalidoException(String message) {
        super(message);
    }

    public MontoInvalidoException(BigDecimal monto) {
        super("Monto inválido: " + monto + ". El monto debe ser mayor a cero.");
    }
}
