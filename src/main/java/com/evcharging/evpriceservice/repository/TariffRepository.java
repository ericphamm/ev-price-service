package com.evcharging.evpriceservice.repository;

import com.evcharging.evpriceservice.entity.TariffStructure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<TariffStructure, Long> {
}
