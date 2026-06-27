package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.CrearReservaRequest;
import pe.edu.upeu.api_restaurant.dto.response.ReservaResponse;
import pe.edu.upeu.api_restaurant.security.JwtUtil;
import pe.edu.upeu.api_restaurant.service.ReservaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@RequestHeader("Authorization") String authHeader,
                                                 @Valid @RequestBody CrearReservaRequest request) {
        UUID clienteId = jwtUtil.extraerUsuarioId(authHeader.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(clienteId, request));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }
}
