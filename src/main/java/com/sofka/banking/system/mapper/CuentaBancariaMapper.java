package com.sofka.banking.system.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import com.sofka.banking.system.dto.response.CuentaBancariaDTO;
import com.sofka.banking.system.entity.CuentaBancaria;

@Mapper(componentModel = "spring")
public interface CuentaBancariaMapper {
    CuentaBancariaDTO toDTO(CuentaBancaria cuentaBancaria);

    List<CuentaBancariaDTO> toDTOList(List<CuentaBancaria> cuentasBancarias);
}
