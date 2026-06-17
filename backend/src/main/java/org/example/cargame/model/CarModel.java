package org.example.cargame;

import org.example.cargame.components.*;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.ModelType;

import org.example.cargame.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CarModel extends Model implements HasStates, HasPositions, HasEngines, HasStorage, HasSpeeds, HasColors, HasPaths, HasMessages {
    private final Map<EntityId, PositionComponent> positions = new ConcurrentHashMap<>();
    private final Map<EntityId, EnergyStorageComponent> storage = new ConcurrentHashMap<>();
    private final Map<EntityId, ColorComponent> colors = new ConcurrentHashMap<>();
    private final Map<EntityId, EngineComponent> engines = new ConcurrentHashMap<>();
    private final Map<EntityId, StateComponent> states = new ConcurrentHashMap<>();
    private final Map<EntityId, SpeedComponent> speeds = new ConcurrentHashMap<>();
    private final Map<EntityId, PathComponent> paths = new ConcurrentHashMap<>();
    private final Map<EntityId, MessageComponent> messages = new ConcurrentHashMap<>();

    @Override
    public void removeEntity(EntityId id) {
        notifyRemoved(id);
        positions.remove(id);
        colors.remove(id);
        engines.remove(id);
        states.remove(id);
        speeds.remove(id);
        paths.remove(id);
        storage.remove(id);
        messages.remove(id);
    }

    @Override
    public ModelType getType() {
        return ModelType.CARMODEL;
    }

    @Override
    public void clear() {
        positions.clear();
        storage.clear();
        colors.clear();
        engines.clear();
        states.clear();
        speeds.clear();
        paths.clear();
        messages.clear();
    }

    public Collection<EntityId> getAllEntities() {
        return List.copyOf(positions.keySet());
    }

    public Map<EntityId, PositionComponent> getPositions() {
        return positions;
    }

    public Map<EntityId, EnergyStorageComponent> getStorage() {
        return storage;
    }

    public Map<EntityId, ColorComponent> getColors() {
        return colors;
    }

    public Map<EntityId, EngineComponent> getEngines() {
        return engines;
    }

    public Map<EntityId, StateComponent> getStates() {
        return states;
    }

    public Map<EntityId, SpeedComponent> getSpeeds() {
        return speeds;
    }

    public Map<EntityId, PathComponent> getPaths() {
        return paths;
    }

    public Map<EntityId, MessageComponent> getMessages() {
        return messages;
    }

}
