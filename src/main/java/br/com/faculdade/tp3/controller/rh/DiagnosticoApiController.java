package br.com.faculdade.tp3.controller.rh;

import br.com.faculdade.tp3.service.FaultSimulationService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rh/diagnostico")
public class DiagnosticoApiController {

    private final FaultSimulationService faultSimulationService;

    public DiagnosticoApiController(FaultSimulationService faultSimulationService) {
        this.faultSimulationService = faultSimulationService;
    }

    @GetMapping("/timeout")
    public ResponseEntity<Map<String, String>> timeout(
            @RequestParam(defaultValue = "100") long delayMs,
            @RequestParam(defaultValue = "200") long timeoutMs
    ) {
        String status = faultSimulationService.simularTimeout(delayMs, timeoutMs);
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/sobrecarga")
    public ResponseEntity<Map<String, String>> sobrecarga(@RequestParam(defaultValue = "1000") long holdMs) {
        String status = faultSimulationService.simularSobrecarga(holdMs);
        return ResponseEntity.ok(Map.of("status", status));
    }
}
