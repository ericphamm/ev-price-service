package com.evcharging.evpriceservice.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class StationState {
    /**
     * Exact time of the report.
     */
    private OffsetDateTime dateTime;

    /**
     * State of the electricity meter (total kWh readout).
     */
    private Double kWh;

    /**
     * Current state of the charger.
     */
    private State state;

    public enum State {
        CHARGING,  // actively charging the vehicle
        PARKING,   // vehicle left connected after charging has concluded
        IDLE       // no vehicle connected
    }
}
