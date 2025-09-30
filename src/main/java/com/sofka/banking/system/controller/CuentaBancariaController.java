package com.sofka.banking.system.controller;

import java.util.List;

import com.sofka.banking.system.service.CuentaBancariaService;
import com.sofka.banking.system.service.impl.CuentaBancariaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cuentas Bancarias", description = "Operaciones sobre cuentas bancarias")
@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaBancariaController {
    private final CuentaBancariaService cuentaBancariaService;

    @Operation(summary = "Crear cuenta bancaria",
            description = "Crea una nueva cuenta bancaria asociada a un usuario.")
    @PostMapping
    public ResponseEntity<CuentaBancariaDTO> crearCuenta(@Valid @RequestBody CreateCuentaBancariaDTO CrearCuentaBancariaDTO) {
        try {
            CuentaBancariaDTO nuevaCuenta = cuentaBancariaService.crearCuenta(CrearCuentaBancariaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Obtener cuentas por usuario",
            description = "Devuelve todas las cuentas bancarias de un usuario.")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CuentaBancariaDTO>> obtenerCuentasPorUsuario(@Parameter(
            description = "ID del usuario", required = true) @PathVariable Long usuarioId) {
        List<CuentaBancariaDTO> cuentas = cuentaBancariaService.obtenerCuentasPorUsuario(usuarioId);
        return ResponseEntity.ok(cuentas);
    }

    @Operation(summary = "Consultar saldo de cuenta",
            description = "Devuelve el saldo actual de una cuenta bancaria.")
    @GetMapping("/{cuentaId}/saldo")
    public ResponseEntity<CuentaBancariaDTO> consultarSaldo(
            @Parameter(description = "ID de la cuenta bancaria",
                    required = true) @PathVariable Long cuentaId) {
        CuentaBancariaDTO cuenta = cuentaBancariaService.consultarSaldo(cuentaId);
        return ResponseEntity.ok(cuenta);
    }
}
