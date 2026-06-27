package pe.edu.upeu.api_restaurant.dto.request;

import pe.edu.upeu.api_restaurant.entity.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroUsuarioRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String contrasena;

    @NotBlank
    private String nombre;

    private String telefono;

    @NotNull
    private RolUsuario rol;
}
