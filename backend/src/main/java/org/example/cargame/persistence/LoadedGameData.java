package org.example.cargame.persistence;

import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.EngineType;
import org.example.cargame.enums.MessageType;
import org.example.cargame.enums.State;
import org.example.cargame.snapshot.*;

import java.util.HashMap;
import java.util.Map;

public record LoadedGameData(
        Map<EntityId, Double> speeds,
        Map<EntityId, PositionSnapshot> positions,
        Map<EntityId, String> colors,
        Map<EntityId, EngineType> engines,
        Map<EntityId, PathSnapshot> paths,
        Map<EntityId, Map<MessageType, String>> messages,
        Map<EntityId, EnergyStorageSnapshot> storage,
        Map<EntityId, State> states
) {
    public LoadedGameData() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }
}
