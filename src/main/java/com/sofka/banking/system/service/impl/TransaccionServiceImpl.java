package com.sofka.banking.system.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sofka.banking.system.dto.request.CreateTransaccionDTO;
import com.sofka.banking.system.dto.request.CreateTransferenciaDTO;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Transaccion;
import com.sofka.banking.system.enums.TipoTransaccion;
import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.exception.transaccion.MontoInvalidoException;
import com.sofka.banking.system.exception.transaccion.SaldoInsuficienteException;
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
        BigDecimal nuevoSaldo =
                tipo == TipoTransaccion.DEPOSITO ? cuenta.getSaldoActual().add(monto)
                        : cuenta.getSaldoActual().subtract(monto);

        cuenta.setSaldoActual(nuevoSaldo);
        cuentaBancariaRepository.save(cuenta);

        // registrar transacción
        Transaccion transaccion = Transaccion.builder().cuentaBancaria(cuenta).monto(monto)
                .tipo(tipo).fecha(LocalDateTime.now()).build();

        Transaccion guardada = transaccionRepository.save(transaccion);

        return transaccionMapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public TransaccionDTO realizarTransferencia(CreateTransferenciaDTO dto) {
        // Validar que las cuentas no sean iguales
        if (dto.getCuentaOrigenId().equals(dto.getCuentaDestinoId())) {
            throw new MontoInvalidoException("No se puede transferir a la misma cuenta");
        }

        // Obtener cuenta origen
        CuentaBancaria cuentaOrigen = cuentaBancariaRepository.findById(dto.getCuentaOrigenId())
                .orElseThrow(() -> new CuentaBancariaNotFoundException(dto.getCuentaOrigenId()));

        // Obtener cuenta destino
        CuentaBancaria cuentaDestino = cuentaBancariaRepository.findById(dto.getCuentaDestinoId())
                .orElseThrow(() -> new CuentaBancariaNotFoundException(dto.getCuentaDestinoId()));

        BigDecimal monto = dto.getMonto();

        // Validar saldo suficiente en cuenta origen
        if (cuentaOrigen.getSaldoActual().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(cuentaOrigen.getSaldoActual(), monto);
        }

        // Realizar la transferencia
        // 1. Restar de cuenta origen
        cuentaOrigen.setSaldoActual(cuentaOrigen.getSaldoActual().subtract(monto));
        cuentaBancariaRepository.save(cuentaOrigen);

        // 2. Sumar a cuenta destino
        cuentaDestino.setSaldoActual(cuentaDestino.getSaldoActual().add(monto));
        cuentaBancariaRepository.save(cuentaDestino);

        // 3. Registrar transacción de transferencia
        Transaccion transaccion = Transaccion.builder().cuentaBancaria(cuentaOrigen)
                .cuentaDestino(cuentaDestino).monto(monto).tipo(TipoTransaccion.TRANSFERENCIA)
                .fecha(LocalDateTime.now()).build();

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
