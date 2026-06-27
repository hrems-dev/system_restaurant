package pe.edu.upeu.api_restaurant.mapper;

import pe.edu.upeu.api_restaurant.dto.response.FacturaResponse;
import pe.edu.upeu.api_restaurant.entity.Factura;
import org.springframework.stereotype.Component;

@Component
public class FacturaMapper {
    public FacturaResponse toResponse(Factura factura) {
        return FacturaResponse.builder()
            .id(factura.getId())
            .numeroFactura(factura.getNumeroFactura())
            .pedidoId(factura.getPedido().getId())
            .nombreCliente(factura.getNombreCliente())
            .rucCliente(factura.getRucCliente())
            .razonSocial(factura.getRazonSocial())
            .subtotal(factura.getSubtotal())
            .igv(factura.getIgv())
            .total(factura.getTotal())
            .emitidaEn(factura.getEmitidaEn())
            .build();
    }
}
