package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Pedido;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByClienteId(UUID clienteId);
}
