package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.OrdenCocina;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenCocinaRepository extends JpaRepository<OrdenCocina, UUID> {
    Optional<OrdenCocina> findByPedidoId(UUID pedidoId);
    List<OrdenCocina> findByEstadoPedidoIn(List<EstadoPedido> estados);
}
