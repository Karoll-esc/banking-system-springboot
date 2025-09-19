package com.sofka.banking.system.service;

import java.util.List;
import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;

public interface TransaccionService {
    TransaccionDTO registrarTransaccion(CreateTransaccionDTO dto);

    List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long cuentaBancariaId);
}
