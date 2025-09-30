package com.sofka.banking.system.exception.usuario;

public class CedulaAlreadyExistsException extends RuntimeException {
    public CedulaAlreadyExistsException(String cedula) {
        super("Ya existe un usuario con la cédula: " + cedula);
    }
}
