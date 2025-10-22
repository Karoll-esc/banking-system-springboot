package com.sofka.banking.system.service.impl;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.request.UpdateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.exception.usuario.CedulaAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.EmailAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.UsuarioNotFoundException;
import com.sofka.banking.system.mapper.UsuarioMapper;
import com.sofka.banking.system.repository.UsuarioRepository;
import com.sofka.banking.system.service.UsuarioService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarioMapper.toDTOList(usuarios);
    }

    @Override
    public UsuarioDTO crearUsuario(CreateUsuarioDTO crearUsuarioDTO) {

        if (usuarioRepository.existsByCedula(crearUsuarioDTO.getCedula())) {
            throw new CedulaAlreadyExistsException(crearUsuarioDTO.getCedula());
        }

        if (usuarioRepository.existsByEmail(crearUsuarioDTO.getEmail())) {
            throw new EmailAlreadyExistsException(crearUsuarioDTO.getEmail());
        }

        // Mapear DTO a entidad (sin password)
        Usuario nuevoUsuario = usuarioMapper.toEntity(crearUsuarioDTO);

        // Hashear y establecer la contraseña
        String passwordHasheada = passwordEncoder.encode(crearUsuarioDTO.getPassword());
        nuevoUsuario.setPassword(passwordHasheada);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        return usuarioMapper.toDTO(usuarioGuardado);
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario =
                usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioDTO actualizarUsuario(Long id, UpdateUsuarioDTO datosActualizados) {

        Usuario usuario =
                usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));

        usuarioMapper.updateEntityFromDTO(usuario, datosActualizados);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public String eliminarUsuario(Long id) {
        // Verificar que el usuario existe y obtenerlo con sus relaciones
        Usuario usuario =
                usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));

        // Debido a la configuración de cascada (CascadeType.ALL) en las relaciones,
        // al eliminar el usuario, automáticamente se eliminarán:
        // 1. Sus cuentas bancarias (OneToMany con cascade = ALL)
        // 2. Las transacciones de esas cuentas (OneToMany en CuentaBancaria con cascade = ALL)
        usuarioRepository.delete(usuario);

        return "Usuario eliminado exitosamente";
    }
}
