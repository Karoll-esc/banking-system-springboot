package com.sofka.banking.system.exception.cuentaBancaria;

public class CuentaBancariaNotFoundException extends RuntimeException {
    public CuentaBancariaNotFoundException(String message) {
        super(message);
    }

    public CuentaBancariaNotFoundException(Long id) {
        super("Cuenta bancaria con ID " + id + " no encontrada");
    }
}