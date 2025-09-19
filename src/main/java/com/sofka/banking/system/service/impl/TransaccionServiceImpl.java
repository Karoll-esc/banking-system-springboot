package com.sofka.banking.system.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Transaccion;
import com.sofka.banking.system.mapper.TransaccionMapper;
import com.sofka.banking.system.repository.CuentaBancariaRepository;
import com.sofka.banking.system.repository.TransaccionRepository;
import com.sofka.banking.system.service.TransaccionService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {
    private final TransaccionRepository transaccionRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final TransaccionMapper transaccionMapper;

    @Override
    @Transactional
    public TransaccionDTO registrarTransaccion(CreateTransaccionDTO dto) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(dto.getCuentaBancariaId())
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada"));
        BigDecimal monto = dto.getMonto();
        Transaccion.TipoTransaccion tipo = Transaccion.TipoTransaccion.valueOf(dto.getTipo());
        if (tipo == Transaccion.TipoTransaccion.RETIRO
                && cuenta.getSaldoActual().compareTo(monto) < 0) {
            throw new RuntimeException("Saldo insuficiente para el retiro");
        }
        // actualizar saldo
        if (tipo == Transaccion.TipoTransaccion.DEPOSITO) {
            cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));
        } else {
            cuenta.setSaldoActual(cuenta.getSaldoActual().subtract(monto));
        }
        cuentaBancariaRepository.save(cuenta);
        // registrar transacción
        Transaccion transaccion = Transaccion.builder().cuentaBancaria(cuenta).monto(monto)
                .tipo(tipo).build();
        Transaccion guardada = transaccionRepository.save(transaccion);
        return transaccionMapper.toDTO(guardada);
    }

    @Override
    public List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long cuentaBancariaId) {
        List<Transaccion> transacciones =
                transaccionRepository.findByCuentaBancariaId(cuentaBancariaId);
        return transaccionMapper.toDTOList(transacciones);
    }
}
