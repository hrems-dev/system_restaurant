package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.response.ReporteResponse;
import pe.edu.upeu.api_restaurant.service.ReporteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final ReporteService reporteService;

    @GetMapping("/ventas")
    public ResponseEntity<ReporteResponse.Ventas> ventas() {
        return ResponseEntity.ok(reporteService.ventas());
    }

    @GetMapping("/ocupacion")
    public ResponseEntity<ReporteResponse.OcupacionMesas> ocupacion() {
        return ResponseEntity.ok(reporteService.ocupacion());
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ReporteResponse.ProductoVendido>> productosMasVendidos() {
        return ResponseEntity.ok(reporteService.productosMasVendidos());
    }
}
