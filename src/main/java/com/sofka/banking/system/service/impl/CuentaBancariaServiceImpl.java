package com.sofka.banking.system.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.exception.cuentaBancaria.CuentaBancariaNotFoundException;
import com.sofka.banking.system.exception.cuentaBancaria.NumeroCuentaAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.UsuarioNotFoundException;
import com.sofka.banking.system.mapper.CuentaBancariaMapper;
import com.sofka.banking.system.repository.CuentaBancariaRepository;
import com.sofka.banking.system.repository.UsuarioRepository;
import com.sofka.banking.system.service.CuentaBancariaService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CuentaBancariaServiceImpl implements CuentaBancariaService {
    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaBancariaMapper cuentaBancariaMapper;

    @Override
    public CuentaBancariaDTO crearCuenta(CreateCuentaBancariaDTO dto) {
        if (cuentaBancariaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new NumeroCuentaAlreadyExistsException(dto.getNumeroCuenta());
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(dto.getUsuarioId()));

        CuentaBancaria cuenta = CuentaBancaria.builder().numeroCuenta(dto.getNumeroCuenta())
                .saldoActual(dto.getSaldoActual()).usuario(usuario).build();
        CuentaBancaria guardada = cuentaBancariaRepository.save(cuenta);
        return cuentaBancariaMapper.toDTO(guardada);
    }

    @Override
    public List<CuentaBancariaDTO> obtenerCuentasPorUsuario(Long usuarioId) {
        List<CuentaBancaria> cuentas = cuentaBancariaRepository.findByUsuarioId(usuarioId);
        return cuentaBancariaMapper.toDTOList(cuentas);
    }

    @Override
    public CuentaBancariaDTO consultarSaldo(Long cuentaId) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaBancariaNotFoundException(cuentaId));
        return cuentaBancariaMapper.toDTO(cuenta);
    }

    @Override
    public String eliminarCuenta(Long cuentaId) {
        if (!cuentaBancariaRepository.existsById(cuentaId)) {
            throw new CuentaBancariaNotFoundException(cuentaId);
        }

        cuentaBancariaRepository.deleteById(cuentaId);
        return "Cuenta eliminada exitosamente";
    }

    @Override
    public CuentaBancariaDTO buscarPorNumeroCuenta(String numeroCuenta) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaBancariaNotFoundException(
                        "Cuenta bancaria con número " + numeroCuenta + " no encontrada"));
        return cuentaBancariaMapper.toDTO(cuenta);
    }
}
