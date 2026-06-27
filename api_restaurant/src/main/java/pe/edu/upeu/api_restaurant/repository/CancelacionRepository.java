package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Cancelacion;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CancelacionRepository extends JpaRepository<Cancelacion, UUID> {
    @Query("select coalesce(sum(c.montoPenalidad), 0) from Cancelacion c")
    BigDecimal sumarPenalidades();
}
