package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.response.ReporteResponse;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPago;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import pe.edu.upeu.api_restaurant.repository.CancelacionRepository;
import pe.edu.upeu.api_restaurant.repository.DetallePedidoRepository;
import pe.edu.upeu.api_restaurant.repository.MesaRepository;
import pe.edu.upeu.api_restaurant.repository.PagoRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final PagoRepository pagoRepository;
    private final MesaRepository mesaRepository;
    private final CancelacionRepository cancelacionRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ReporteResponse.Ventas ventas() {
        BigDecimal adelantos = pagoRepository.sumarPorEstadoYTipo(EstadoPago.CONFIRMADO, TipoPago.ADELANTO_RESERVA);
        BigDecimal consumo = pagoRepository.sumarPorEstadoYTipo(EstadoPago.CONFIRMADO, TipoPago.CONSUMO_FINAL);
        BigDecimal delivery = pagoRepository.sumarPorEstadoYTipo(EstadoPago.CONFIRMADO, TipoPago.DELIVERY);
        BigDecimal penalidades = cancelacionRepository.sumarPenalidades();
        return ReporteResponse.Ventas.builder()
            .periodo("general")
            .totalAdelantos(adelantos)
            .totalConsumoEnMesa(consumo)
            .totalDelivery(delivery)
            .totalPenalidades(penalidades)
            .totalGeneral(adelantos.add(consumo).add(delivery).add(penalidades))
            .build();
    }

    public ReporteResponse.OcupacionMesas ocupacion() {
        long total = mesaRepository.count();
        long disponibles = mesaRepository.countByEstado(EstadoMesa.DISPONIBLE);
        long ocupadas = mesaRepository.countByEstado(EstadoMesa.OCUPADA);
        long reservadas = mesaRepository.countByEstado(EstadoMesa.RESERVADA);
        long pendientesPago = mesaRepository.countByEstado(EstadoMesa.PENDIENTE_PAGO);
        double porcentaje = total == 0 ? 0 : ((double) (ocupadas + reservadas + pendientesPago) / total) * 100;
        return ReporteResponse.OcupacionMesas.builder()
            .totalMesas(total)
            .disponibles(disponibles)
            .ocupadas(ocupadas)
            .reservadas(reservadas)
            .pendientesPago(pendientesPago)
            .porcentajeOcupacion(porcentaje)
            .build();
    }

    public List<ReporteResponse.ProductoVendido> productosMasVendidos() {
        return detallePedidoRepository.productosMasVendidos().stream()
            .map(row -> ReporteResponse.ProductoVendido.builder()
                .nombreProducto((String) row[0])
                .totalUnidades(((Number) row[1]).longValue())
                .build())
            .toList();
    }
}
