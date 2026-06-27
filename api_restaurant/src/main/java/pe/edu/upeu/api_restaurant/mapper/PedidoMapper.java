package pe.edu.upeu.api_restaurant.mapper;

import pe.edu.upeu.api_restaurant.dto.response.PedidoResponse;
import pe.edu.upeu.api_restaurant.entity.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {
    public PedidoResponse toResponse(Pedido pedido) {
        return PedidoResponse.builder()
            .id(pedido.getId())
            .estado(pedido.getEstado())
            .total(pedido.getTotal())
            .esDelivery(pedido.isEsDelivery())
            .creadoEn(pedido.getCreadoEn())
            .build();
    }
}
