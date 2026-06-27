package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.ActualizarEstadoPedidoRequest;
import pe.edu.upeu.api_restaurant.dto.request.CrearPedidoRequest;
import pe.edu.upeu.api_restaurant.dto.request.DetallePedidoRequest;
import pe.edu.upeu.api_restaurant.dto.response.PedidoResponse;
import pe.edu.upeu.api_restaurant.entity.DetallePedido;
import pe.edu.upeu.api_restaurant.entity.Mesa;
import pe.edu.upeu.api_restaurant.entity.OrdenCocina;
import pe.edu.upeu.api_restaurant.entity.Pedido;
import pe.edu.upeu.api_restaurant.entity.ProductoMenu;
import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.exception.ReglaNegocioException;
import pe.edu.upeu.api_restaurant.mapper.PedidoMapper;
import pe.edu.upeu.api_restaurant.repository.MesaRepository;
import pe.edu.upeu.api_restaurant.repository.OrdenCocinaRepository;
import pe.edu.upeu.api_restaurant.repository.PedidoRepository;
import pe.edu.upeu.api_restaurant.repository.ProductoMenuRepository;
import pe.edu.upeu.api_restaurant.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ProductoMenuRepository productoMenuRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrdenCocinaRepository ordenCocinaRepository;
    private final PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponse crear(UUID clienteId, CrearPedidoRequest request) {
        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
        Mesa mesa = null;
        if (!request.isEsDelivery()) {
            if (request.getMesaId() == null) {
                throw new ReglaNegocioException("El pedido en mesa requiere mesaId");
            }
            mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada"));
            mesa.setEstado(EstadoMesa.OCUPADA);
        }
        Pedido pedido = Pedido.builder()
            .cliente(cliente)
            .mesa(mesa)
            .esDelivery(request.isEsDelivery())
            .direccionDelivery(request.getDireccionDelivery())
            .notas(request.getNotas())
            .estado(EstadoPedido.ENVIADO_COCINA)
            .build();

        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedidoRequest detalleRequest : request.getDetalles()) {
            ProductoMenu producto = productoMenuRepository.findById(detalleRequest.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
            if (!producto.isDisponible()) {
                throw new ReglaNegocioException("Producto no disponible: " + producto.getNombre());
            }
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));
            pedido.getDetalles().add(DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(detalleRequest.getCantidad())
                .precioUnitario(producto.getPrecio())
                .subtotal(subtotal)
                .observaciones(detalleRequest.getObservaciones())
                .build());
            total = total.add(subtotal);
        }
        pedido.setTotal(total);
        Pedido guardado = pedidoRepository.save(pedido);
        ordenCocinaRepository.save(OrdenCocina.builder()
            .pedido(guardado)
            .estadoPedido(EstadoPedido.ENVIADO_COCINA)
            .prioridad(request.isEsDelivery() ? 2 : 1)
            .build());
        return pedidoMapper.toResponse(guardado);
    }

    @Transactional
    public PedidoResponse actualizarEstado(UUID pedidoId, ActualizarEstadoPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
        pedido.setEstado(request.getEstado());
        ordenCocinaRepository.findByPedidoId(pedidoId).ifPresent(orden -> orden.setEstadoPedido(request.getEstado()));
        return pedidoMapper.toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidoRepository.findAll().stream().map(pedidoMapper::toResponse).toList();
    }
}
