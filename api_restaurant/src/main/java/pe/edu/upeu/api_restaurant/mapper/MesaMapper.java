package pe.edu.upeu.api_restaurant.mapper;

import pe.edu.upeu.api_restaurant.dto.response.MesaResponse;
import pe.edu.upeu.api_restaurant.entity.CodigoQR;
import pe.edu.upeu.api_restaurant.entity.Mesa;
import org.springframework.stereotype.Component;

@Component
public class MesaMapper {
    public MesaResponse toResponse(Mesa mesa) {
        CodigoQR qr = mesa.getCodigoQR();
        return MesaResponse.builder()
            .id(mesa.getId())
            .numero(mesa.getNumero())
            .capacidad(mesa.getCapacidad())
            .ubicacion(mesa.getUbicacion())
            .estado(mesa.getEstado())
            .tokenQR(qr != null ? qr.getToken() : null)
            .urlQR(qr != null ? qr.getUrlAcceso() : null)
            .imagenQRBase64(qr != null ? qr.getImagenBase64() : null)
            .build();
    }
}
