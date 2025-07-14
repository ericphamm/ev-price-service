package com.evcharging.evpriceservice.service;

import com.evcharging.evpriceservice.entity.TariffComponent;
import com.evcharging.evpriceservice.entity.TariffStructure;
import com.evcharging.evpriceservice.model.PriceCalculationRequest;
import com.evcharging.evpriceservice.model.StationState;
import com.evcharging.evpriceservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TariffService {

    private final TariffRepository tariffRepository;

    public TariffService(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    public TariffStructure saveTariff(TariffStructure tariff) {
        for (TariffComponent component : tariff.getComponents()) {
            component.setTariff(tariff);
        }
        return tariffRepository.save(tariff);
    }

    public List<TariffStructure> getAllTariffs() {
        return tariffRepository.findAll();
    }

    public void deleteTariff(Long id) {
        if (!tariffRepository.existsById(id)) {
            throw new EntityNotFoundException("Tariff with id " + id + " not found.");
        }
        tariffRepository.deleteById(id);
    }

    public TariffStructure updatedTariff(Long id, TariffStructure updatedTariff) {
        TariffStructure existing = tariffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tariff not found with id" + id));
        existing.setName(updatedTariff.getName());

        existing.getComponents().clear();
        for (TariffComponent component : updatedTariff.getComponents()) {
            component.setTariff(existing);
            existing.getComponents().add(component);
        }
        return tariffRepository.save(existing);
    }

    public BigDecimal calculatePrice(PriceCalculationRequest request) {
        Long tariffId = request.getTariffId();
        List<StationState> states = request.getStates();

        if (states == null || states.size() < 2) {
            throw new IllegalArgumentException("The list of states must contain at least ");
        }

        TariffStructure tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found with id: " + tariffId));

        BigDecimal total = BigDecimal.ZERO;

        for (TariffComponent component : tariff.getComponents()) {
            BigDecimal componentCost = BigDecimal.ZERO;

            switch (component.getType()) {
                case KWH -> {
                    BigDecimal energyUsed = BigDecimal.valueOf(
                            states.get(states.size() - 1).getKWh() - states.get(0).getKWh()
                    );
                    BigDecimal billableEnergy = energyUsed.subtract(component.getFreeUnits()).max(BigDecimal.ZERO);
                    componentCost = component.getPrice().multiply(billableEnergy);
                }

                case CHARGING_MINUTE -> {
                    long minutes = 0;
                    if (states.isEmpty() && states == null) {
                        throw new IllegalArgumentException("List of states cant be empty");
                    }
                    for (int i = 1; i < states.size(); i++) {
                        StationState prev = states.get(i - 1);
                        StationState curr = states.get(i);
                        if (prev.getState() == StationState.State.CHARGING) {
                            long diff = java.time.Duration.between(prev.getDateTime(), curr.getDateTime()).toMinutes();
                            minutes += diff;
                        }
                    }
                    long billable = Math.max(0, minutes - component.getFreeUnits().longValue());
                    componentCost = component.getPrice().multiply(BigDecimal.valueOf(billable));
                }

                case PARKING_MINUTE -> {
                    long minutes = 0;
                    for (int i = 1; i < states.size(); i++) {
                        StationState prev = states.get(i - 1);
                        StationState curr = states.get(i);
                        if (prev.getState() == StationState.State.PARKING) {
                            long diff = java.time.Duration.between(prev.getDateTime(), curr.getDateTime()).toMinutes();
                            minutes += diff;
                        }
                    }
                    long billable = Math.max(0, minutes - component.getFreeUnits().longValue());
                    componentCost = component.getPrice().multiply(BigDecimal.valueOf(billable));
                }
            }

            total = total.add(componentCost);
        }

        return total;
    }
}
