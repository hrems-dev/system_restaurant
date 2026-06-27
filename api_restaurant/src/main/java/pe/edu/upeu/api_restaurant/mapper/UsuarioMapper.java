package pe.edu.upeu.api_restaurant.mapper;

import pe.edu.upeu.api_restaurant.dto.response.AuthResponse;
import pe.edu.upeu.api_restaurant.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public AuthResponse toAuthResponse(Usuario usuario, String token) {
        return AuthResponse.builder()
            .token(token)
            .tipo("Bearer")
            .usuarioId(usuario.getId())
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .rol(usuario.getRol())
            .build();
    }
}
