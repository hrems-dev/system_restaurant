package pe.edu.upeu.api_restaurant.dto.response;

import pe.edu.upeu.api_restaurant.entity.enums.RolUsuario;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String tipo;
    private UUID usuarioId;
    private String email;
    private String nombre;
    private RolUsuario rol;
}
