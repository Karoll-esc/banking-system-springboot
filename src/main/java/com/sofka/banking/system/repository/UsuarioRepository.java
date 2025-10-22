package com.sofka.banking.system.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sofka.banking.system.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCedula(String cedula);

    boolean existsByEmail(String email);

    // Nuevo método para login
    Optional<Usuario> findByCedula(String cedula);
}
