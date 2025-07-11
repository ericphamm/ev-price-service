package com.evcharging.evpriceservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tariff_component")
public class TariffComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private BigDecimal price;
    private BigDecimal freeUnits;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    @JsonBackReference
    private TariffStructure tariff;

    public enum Type {
        KWH,             // Energy consumption
        CHARGING_MINUTE, // Time spent charging
        PARKING_MINUTE   // Time spent parking
    }

    // getters a setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFreeUnits() {
        return freeUnits;
    }

    public void setFreeUnits(BigDecimal freeUnits) {
        this.freeUnits = freeUnits;
    }

    public TariffStructure getTariff() {
        return tariff;
    }

    public void setTariff(TariffStructure tariff) {
        this.tariff = tariff;
    }
}
