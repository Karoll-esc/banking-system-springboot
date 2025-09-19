package com.sofka.banking.system.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.sofka.banking.system.dto.request.CreateCuentaBancariaDTO;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import com.sofka.banking.system.entity.CuentaBancaria;
import com.sofka.banking.system.entity.Usuario;
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
            throw new RuntimeException("El número de cuenta ya existe");
        }
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
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
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return cuentaBancariaMapper.toDTO(cuenta);
    }
}
