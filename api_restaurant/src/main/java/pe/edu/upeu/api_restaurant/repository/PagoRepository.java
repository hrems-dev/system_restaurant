package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Pago;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPago;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
    @Query("select coalesce(sum(p.monto), 0) from Pago p where p.estado = :estado and p.tipo = :tipo")
    BigDecimal sumarPorEstadoYTipo(@Param("estado") EstadoPago estado, @Param("tipo") TipoPago tipo);
}
