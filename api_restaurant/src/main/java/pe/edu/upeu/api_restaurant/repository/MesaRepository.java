package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Mesa;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<Mesa, UUID> {
    Optional<Mesa> findByNumero(Integer numero);
    boolean existsByNumero(Integer numero);
    long countByEstado(EstadoMesa estado);
    List<Mesa> findByEstado(EstadoMesa estado);
}
