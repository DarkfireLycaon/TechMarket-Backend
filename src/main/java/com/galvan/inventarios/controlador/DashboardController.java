package com.galvan.inventarios.controlador;


import com.galvan.inventarios.dto.DashboardDTO;
import com.galvan.inventarios.servicio.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardDTO> getDatos() {
        return ResponseEntity.ok(dashboardService.obtenerDatosDashboard());
    }
}
