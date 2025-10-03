package com.sofka.banking.system.service;

import java.util.List;
import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;

public interface CuentaBancariaService {
    CuentaBancariaDTO crearCuenta(CreateCuentaBancariaDTO dto);

    List<CuentaBancariaDTO> obtenerCuentasPorUsuario(Long usuarioId);

    CuentaBancariaDTO consultarSaldo(Long cuentaId);

    String eliminarCuenta(Long cuentaId);
}
