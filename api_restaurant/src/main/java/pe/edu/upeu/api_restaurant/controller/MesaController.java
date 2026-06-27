package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.CrearMesaRequest;
import pe.edu.upeu.api_restaurant.dto.response.MesaResponse;
import pe.edu.upeu.api_restaurant.service.MesaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mesas")
@RequiredArgsConstructor
public class MesaController {
    private final MesaService mesaService;

    @PostMapping
    public ResponseEntity<MesaResponse> crear(@Valid @RequestBody CrearMesaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<MesaResponse>> listar() {
        return ResponseEntity.ok(mesaService.listar());
    }

    @GetMapping("/qr/{token}")
    public ResponseEntity<MesaResponse> buscarPorQr(@PathVariable String token) {
        return ResponseEntity.ok(mesaService.buscarPorQr(token));
    }
}
