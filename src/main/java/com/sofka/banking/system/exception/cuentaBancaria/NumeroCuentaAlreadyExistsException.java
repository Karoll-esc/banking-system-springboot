package com.sofka.banking.system.exception.cuentaBancaria;

public class NumeroCuentaAlreadyExistsException extends RuntimeException {
    public NumeroCuentaAlreadyExistsException(String numeroCuenta) {
        super("Ya existe una cuenta con el número: " + numeroCuenta);
    }
}
