package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.ActualizarEstadoPedidoRequest;
import pe.edu.upeu.api_restaurant.dto.request.CrearPedidoRequest;
import pe.edu.upeu.api_restaurant.dto.response.PedidoResponse;
import pe.edu.upeu.api_restaurant.security.JwtUtil;
import pe.edu.upeu.api_restaurant.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@RequestHeader("Authorization") String authHeader,
                                                @Valid @RequestBody CrearPedidoRequest request) {
        UUID clienteId = jwtUtil.extraerUsuarioId(authHeader.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(clienteId, request));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar() {
        return ResponseEntity.ok(pedidoService.listar());
    }

    @PatchMapping("/{pedidoId}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(@PathVariable UUID pedidoId,
                                                           @Valid @RequestBody ActualizarEstadoPedidoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(pedidoId, request));
    }
}
