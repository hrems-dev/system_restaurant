package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.entity.enums.RolUsuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByRolAndActivoTrue(RolUsuario rol);
}
