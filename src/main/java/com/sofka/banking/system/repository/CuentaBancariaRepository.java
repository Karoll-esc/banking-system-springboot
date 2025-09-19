package com.sofka.banking.system.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sofka.banking.system.entity.CuentaBancaria;

public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {
    List<CuentaBancaria> findByUsuarioId(Long usuarioId);

    boolean existsByNumeroCuenta(String numeroCuenta);
}
