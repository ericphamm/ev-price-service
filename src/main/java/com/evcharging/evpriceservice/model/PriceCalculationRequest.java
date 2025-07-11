package com.evcharging.evpriceservice.model;

import java.util.List;

public class PriceCalculationRequest {
    private Long tariffId;

    private List<StationState> states;

    //getters and setters
    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }

    public List<StationState> getStates() {
        return states;
    }

    public void setStates(List<StationState> states) {
        this.states = states;
    }
}
