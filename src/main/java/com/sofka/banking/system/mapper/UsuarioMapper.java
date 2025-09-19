package com.sofka.banking.system.mapper;

import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import com.sofka.banking.system.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")  // Para que funcione con Spring
public interface UsuarioMapper {

    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    Usuario toEntity(CreateUsuarioDTO crearUsuarioDTO);

    List<UsuarioDTO> toDTOList(List<Usuario> usuarios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cedula", ignore = true)
    void updateEntityFromDTO(@MappingTarget Usuario usuario, CreateUsuarioDTO dto);
}