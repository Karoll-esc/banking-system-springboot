package com.sofka.banking.system.dto.response;

import com.sofka.banking.system.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
