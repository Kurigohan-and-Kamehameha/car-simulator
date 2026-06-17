package org.example.cargame.snapshot;

import org.example.cargame.enums.EngineType;
import org.example.cargame.enums.State;

public record GameStateDTO(
        int id,
        double x,
        double y,
        String color,
        EngineType engine,
        double power,
        State state,
        String message,
        String warning,
        double speed
) {}
