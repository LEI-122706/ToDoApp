package com.example.cambio;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CambioMoedasController {
    private final CambioMoedas cambioMoedas = new CambioMoedas();

    @GetMapping("/api/converter")
    public double converterMoeda(@RequestParam double valor, @RequestParam double taxaCambio) {
        return cambioMoedas.converter(valor, taxaCambio);
    }
}
