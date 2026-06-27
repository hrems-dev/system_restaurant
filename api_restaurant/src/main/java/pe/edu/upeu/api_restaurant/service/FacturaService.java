package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.response.FacturaResponse;
import pe.edu.upeu.api_restaurant.entity.Factura;
import pe.edu.upeu.api_restaurant.entity.Pedido;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.mapper.FacturaMapper;
import pe.edu.upeu.api_restaurant.repository.FacturaRepository;
import pe.edu.upeu.api_restaurant.repository.PedidoRepository;
import pe.edu.upeu.api_restaurant.util.ConstantesNegocio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FacturaService {
    private final FacturaRepository facturaRepository;
    private final PedidoRepository pedidoRepository;
    private final FacturaMapper facturaMapper;

    @Transactional
    public FacturaResponse emitir(UUID pedidoId) {
        return facturaRepository.findByPedidoId(pedidoId)
            .map(facturaMapper::toResponse)
            .orElseGet(() -> crearFactura(pedidoId));
    }

    private FacturaResponse crearFactura(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
        BigDecimal total = pedido.getTotal();
        BigDecimal subtotal = total.divide(BigDecimal.ONE.add(ConstantesNegocio.IGV), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);
        Factura factura = facturaRepository.save(Factura.builder()
            .numeroFactura("F001-" + System.currentTimeMillis())
            .pedido(pedido)
            .nombreCliente(pedido.getCliente() != null ? pedido.getCliente().getNombre() : "Consumidor final")
            .subtotal(subtotal)
            .igv(igv)
            .total(total)
            .build());
        return facturaMapper.toResponse(factura);
    }
}
