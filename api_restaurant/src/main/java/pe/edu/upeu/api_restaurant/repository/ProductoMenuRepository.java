package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.ProductoMenu;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoMenuRepository extends JpaRepository<ProductoMenu, UUID> {
    List<ProductoMenu> findByDisponibleTrue();
    List<ProductoMenu> findByCategoriaId(UUID categoriaId);
}
