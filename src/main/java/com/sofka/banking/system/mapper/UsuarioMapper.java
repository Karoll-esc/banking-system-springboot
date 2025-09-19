package com.sofka.banking.system.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import com.sofka.banking.system.dto.request.CreateUsuarioDTO;
import com.sofka.banking.system.dto.response.UsuarioDTO;
import com.sofka.banking.system.entity.Usuario;

@Mapper(componentModel = "spring") 
public interface UsuarioMapper {

    CuentaBancariaMapper cuentaBancariaMapper = Mappers.getMapper(CuentaBancariaMapper.class);

    @Mapping(target = "cuentasBancarias",
            expression = "java(cuentaBancariaMapper.toDTOList(usuario.getCuentasBancarias()))")
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuentasBancarias", ignore = true)
    Usuario toEntity(CreateUsuarioDTO crearUsuarioDTO);

    List<UsuarioDTO> toDTOList(List<Usuario> usuarios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cedula", ignore = true)
    @Mapping(target = "cuentasBancarias", ignore = true)
    void updateEntityFromDTO(@MappingTarget Usuario usuario, CreateUsuarioDTO dto);
}
