package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Reserva;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    List<Reserva> findByClienteId(UUID clienteId);

    @Query("""
        select count(r) > 0 from Reserva r
        where r.mesa.id = :mesaId
          and r.estado in :estados
          and r.fechaHoraInicio < :fin
          and r.fechaHoraFin > :inicio
        """)
    boolean existeSolapamiento(
        @Param("mesaId") UUID mesaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("estados") List<EstadoReserva> estados
    );
}
