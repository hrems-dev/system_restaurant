package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.CodigoQR;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodigoQRRepository extends JpaRepository<CodigoQR, UUID> {
    Optional<CodigoQR> findByTokenAndActivoTrue(String token);
}
