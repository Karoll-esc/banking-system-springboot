package com.sofka.banking.system.service;

import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> obtenerTodosLosUsuarios();

    UsuarioDTO crearUsuario(CreateUsuarioDTO crearUsuarioDTO);

    UsuarioDTO obtenerUsuarioPorId(Long id);

    UsuarioDTO actualizarUsuario(Long id, CreateUsuarioDTO datosActualizados);

    String eliminarUsuario(Long id);

    /*List<UsuarioDTO> buscarUsuariosPorNombre(String nombre);*/
}
