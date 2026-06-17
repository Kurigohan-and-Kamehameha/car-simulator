package org.example.cargame.observer;

import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.EngineType;
import org.example.cargame.snapshot.EnergyStorage;
import org.example.cargame.snapshot.GameStateDTO;
import org.example.cargame.snapshot.PositionSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameStateView implements Observer {
    private final Map<EntityId, GameStateDTO> cache = new ConcurrentHashMap<>();

    private final PositionView positionView;
    private final StateView stateView;
    private final SpeedView speedView;
    private final StorageView storageView;
    private final EngineView engineView;
    private final ColorView colorView;
    private final MessageView messageView;

    public GameStateView(
            PositionView positionView,
            StateView stateView,
            SpeedView speedView,
            StorageView storageView,
            EngineView engineView,
            ColorView colorView,
            MessageView messageView) {
        this.positionView = positionView;
        this.stateView = stateView;
        this.speedView = speedView;
        this.storageView = storageView;
        this.engineView = engineView;
        this.colorView = colorView;
        this.messageView = messageView;

        positionView.addObserver(this);
        stateView.addObserver(this);
        speedView.addObserver(this);
        storageView.addObserver(this);
        engineView.addObserver(this);
        colorView.addObserver(this);
        messageView.addObserver(this);
    }

    public GameStateDTO get(EntityId id) {
        return cache.get(id);
    }

    public Map<EntityId, GameStateDTO> getAll() {
        return Map.copyOf(cache);
    }

    @Override
    public void update(EntityId id) {
        PositionSnapshot snapPos = positionView.getPosition(id);
        if (snapPos == null) {
            return;
        }
        EngineType engineType = engineView.getEngineType(id);
        if (engineType == null) {
            return;
        }
        EnergyStorage energyStorage = storageView.getEnergyStorages(id).get(engineType);
        if (energyStorage == null) {
            return;
        }

        GameStateDTO dto = new GameStateDTO(
                id.getId(),
                snapPos.x(),
                snapPos.y(),
                colorView.getColor(id),
                engineType,
                energyStorage.getPercentage100(),
                stateView.getState(id),
                messageView.alert(id),
                messageView.warning(id),
                speedView.getSpeed(id));
        cache.put(id, dto);
    }

    public void remove(EntityId id) {
        cache.remove(id);
    }

    public void clear() {
        cache.clear();
    }

}
