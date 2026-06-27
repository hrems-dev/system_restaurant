package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.DetallePedido;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, UUID> {
    @Query("""
        select d.producto.nombre, coalesce(sum(d.cantidad), 0)
        from DetallePedido d
        group by d.producto.nombre
        order by coalesce(sum(d.cantidad), 0) desc
        """)
    List<Object[]> productosMasVendidos();
}
