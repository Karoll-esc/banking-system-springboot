package com.sofka.banking.system.repository;

import com.sofka.banking.system.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCedula(String cedula);

    boolean existsByEmail(String email);
}
