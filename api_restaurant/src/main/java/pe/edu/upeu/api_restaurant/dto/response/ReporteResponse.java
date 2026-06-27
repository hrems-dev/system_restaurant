package pe.edu.upeu.api_restaurant.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

public class ReporteResponse {
    @Data
    @Builder
    public static class Ventas {
        private String periodo;
        private BigDecimal totalAdelantos;
        private BigDecimal totalConsumoEnMesa;
        private BigDecimal totalDelivery;
        private BigDecimal totalGeneral;
        private BigDecimal totalPenalidades;
    }

    @Data
    @Builder
    public static class OcupacionMesas {
        private long totalMesas;
        private long disponibles;
        private long ocupadas;
        private long reservadas;
        private long pendientesPago;
        private double porcentajeOcupacion;
    }

    @Data
    @Builder
    public static class ProductoVendido {
        private String nombreProducto;
        private long totalUnidades;
    }
}
