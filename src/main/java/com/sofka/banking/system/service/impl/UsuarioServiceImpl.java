package com.sofka.banking.system.service.impl;

import java.util.List;

import com.sofka.banking.system.exception.usuario.CedulaAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.EmailAlreadyExistsException;
import com.sofka.banking.system.exception.usuario.UsuarioNotFoundException;
import org.springframework.stereotype.Service;
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.request.UpdateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import com.sofka.banking.system.entity.Usuario;
import com.sofka.banking.system.mapper.UsuarioMapper;
import com.sofka.banking.system.repository.UsuarioRepository;
import com.sofka.banking.system.service.UsuarioService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

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

        Usuario nuevoUsuario = usuarioMapper.toEntity(crearUsuarioDTO);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        return usuarioMapper.toDTO(usuarioGuardado);
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioDTO actualizarUsuario(Long id, UpdateUsuarioDTO datosActualizados) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioMapper.updateEntityFromDTO(usuario, datosActualizados);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuarioActualizado);
    }

    @Override
    public String eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        usuarioRepository.deleteById(id);

        return "Usuario eliminado exitosamente";
    }
}
