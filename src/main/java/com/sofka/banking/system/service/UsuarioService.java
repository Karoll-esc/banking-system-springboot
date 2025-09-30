package com.sofka.banking.system.service;

import java.util.List;
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.request.UpdateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;

public interface UsuarioService {
    List<UsuarioDTO> obtenerTodosLosUsuarios();

    UsuarioDTO crearUsuario(CreateUsuarioDTO crearUsuarioDTO);

    UsuarioDTO obtenerUsuarioPorId(Long id);

    UsuarioDTO actualizarUsuario(Long id, UpdateUsuarioDTO datosActualizados);

    String eliminarUsuario(Long id);

    /* List<UsuarioDTO> buscarUsuariosPorNombre(String nombre); */
}
