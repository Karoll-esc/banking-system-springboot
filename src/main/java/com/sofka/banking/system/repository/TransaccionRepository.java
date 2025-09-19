package com.sofka.banking.system.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sofka.banking.system.entity.Transaccion;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCuentaBancariaId(Long cuentaBancariaId);
}
