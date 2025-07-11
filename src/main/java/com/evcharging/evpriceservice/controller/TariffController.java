package com.evcharging.evpriceservice.controller;

import com.evcharging.evpriceservice.entity.TariffStructure;
import com.evcharging.evpriceservice.model.PriceCalculationRequest;
import com.evcharging.evpriceservice.service.TariffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tariffs")
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping
    public ResponseEntity<TariffStructure> saveTariff(@RequestBody TariffStructure tariff) {
        TariffStructure savedTariff = tariffService.saveTariff(tariff);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTariff);
    }

    @GetMapping
    public ResponseEntity<List<TariffStructure>> getAllTariffs() {
        List<TariffStructure> tariffs = tariffService.getAllTariffs();
        return ResponseEntity.ok(tariffs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariff(@PathVariable Long id) {
        tariffService.deleteTariff(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TariffStructure> updateTariff(@PathVariable Long id, @Valid @RequestBody TariffStructure updatedTariff) {
        TariffStructure updated = tariffService.updatedTariff(id, updatedTariff);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/calculate")
    public ResponseEntity<BigDecimal> calculatePrice(@RequestBody PriceCalculationRequest request) {
        BigDecimal result = tariffService.calculatePrice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
