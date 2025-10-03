package com.sofka.banking.system.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import com.sofka.banking.system.service.CuentaBancariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@Tag(name = "Cuentas Bancarias", description = "Operaciones sobre cuentas bancarias")
@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaBancariaController {
    private final CuentaBancariaService cuentaBancariaService;

    @Operation(summary = "Crear cuenta bancaria",
            description = "Crea una nueva cuenta bancaria asociada a un usuario.")
    @PostMapping
    public ResponseEntity<CuentaBancariaDTO> crearCuenta(
            @Valid @RequestBody CreateCuentaBancariaDTO crearCuentaBancariaDTO) {
        CuentaBancariaDTO nuevaCuenta = cuentaBancariaService.crearCuenta(crearCuentaBancariaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
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

    @Operation(summary = "Eliminar cuenta bancaria",
            description = "Elimina una cuenta bancaria por su ID.")
    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<Map<String, String>> eliminarCuenta(
            @Parameter(description = "ID de la cuenta bancaria a eliminar",
                    required = true) @PathVariable Long cuentaId) {
        String mensaje = cuentaBancariaService.eliminarCuenta(cuentaId);
        return ResponseEntity.ok(Map.of("message", mensaje));
    }
}
