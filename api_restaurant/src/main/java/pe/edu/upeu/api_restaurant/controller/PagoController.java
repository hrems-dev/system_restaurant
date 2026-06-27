package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.PagoRequest;
import pe.edu.upeu.api_restaurant.dto.response.PagoResponse;
import pe.edu.upeu.api_restaurant.security.JwtUtil;
import pe.edu.upeu.api_restaurant.service.PagoService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService pagoService;
    private final JwtUtil jwtUtil;

    @PostMapping("/procesar")
    public ResponseEntity<PagoResponse> procesar(@RequestHeader("Authorization") String authHeader,
                                                 @Valid @RequestBody PagoRequest request) {
        UUID clienteId = jwtUtil.extraerUsuarioId(authHeader.substring(7));
        return ResponseEntity.ok(pagoService.procesar(clienteId, request));
    }
}
