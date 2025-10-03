package com.sofka.banking.system.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.sofka.banking.system.dto.response.TransaccionDTO;
import com.sofka.banking.system.entity.Transaccion;

@Mapper(componentModel = "spring")
public interface TransaccionMapper {
    @Mapping(target = "cuentaBancariaId", source = "cuentaBancaria.id")
    @Mapping(target = "cuentaDestinoId", source = "cuentaDestino.id")
    TransaccionDTO toDTO(Transaccion transaccion);

    List<TransaccionDTO> toDTOList(List<Transaccion> transacciones);
}
