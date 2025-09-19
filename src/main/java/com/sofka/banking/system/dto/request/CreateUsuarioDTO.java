package com.sofka.banking.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUsuarioDTO {
    private String cedula;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
