package com.sofka.banking.system.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.exception.transaccion.MontoInvalidoException;
import com.sofka.banking.system.exception.transaccion.SaldoInsuficienteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Transaccion;
import com.sofka.banking.system.enums.TipoTransaccion;
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
        // buscar cuenta
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(dto.getCuentaBancariaId())
                .orElseThrow(() -> new CuentaBancariaNotFoundException(dto.getCuentaBancariaId()));

        BigDecimal monto = dto.getMonto();
        TipoTransaccion tipo = dto.getTipo();

        // validar saldo suficiente para retiros
        if (tipo == TipoTransaccion.RETIRO && cuenta.getSaldoActual().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(cuenta.getSaldoActual(), monto);
        }

        // actualizar saldo
        BigDecimal nuevoSaldo = tipo == TipoTransaccion.DEPOSITO
                ? cuenta.getSaldoActual().add(monto)
                : cuenta.getSaldoActual().subtract(monto);

        cuenta.setSaldoActual(nuevoSaldo);
        cuentaBancariaRepository.save(cuenta);

        // registrar transacción
        Transaccion transaccion = Transaccion.builder()
                .cuentaBancaria(cuenta)
                .monto(monto)
                .tipo(tipo)
                .fecha(LocalDateTime.now())
                .build();

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
