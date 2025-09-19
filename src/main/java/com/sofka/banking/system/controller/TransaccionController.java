package com.sofka.banking.system.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Transacciones", description = "Operaciones sobre transacciones bancarias")
@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
public class TransaccionController {
    private final TransaccionService transaccionService;

    @Operation(summary = "Registrar transacción",
            description = "Registra un depósito o retiro en una cuenta bancaria y actualiza el saldo.")
    @PostMapping
    public ResponseEntity<TransaccionDTO> registrarTransaccion(
            @RequestBody CreateTransaccionDTO dto) {
        TransaccionDTO transaccion = transaccionService.registrarTransaccion(dto);
        return new ResponseEntity<>(transaccion, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener transacciones por cuenta",
            description = "Devuelve todas las transacciones realizadas en una cuenta bancaria.")
    @GetMapping("/cuenta/{cuentaBancariaId}")
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorCuenta(
            @PathVariable Long cuentaBancariaId) {
        List<TransaccionDTO> transacciones =
                transaccionService.obtenerTransaccionesPorCuenta(cuentaBancariaId);
        return ResponseEntity.ok(transacciones);
    }
}
