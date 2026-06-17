package org.example.cargame.persistence;

import jakarta.persistence.Embeddable;

@Embeddable
public record EnergyStorageData(double power, double capacity) {
}