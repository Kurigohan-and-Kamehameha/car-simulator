package org.example.cargame.factories;

import org.example.cargame.model.CarModel;
import org.example.cargame.components.*;
import org.example.cargame.engine.Engine;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.EngineType;
import org.example.cargame.enums.MessageType;
import org.example.cargame.enums.NodeType;
import org.example.cargame.enums.State;
import org.example.cargame.graph.Graph;
import org.example.cargame.graph.Node;
import org.example.cargame.snapshot.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CarFactory {
    private final EngineFactory engineFactory;
    private final Graph graph;

    public CarFactory(EngineFactory engineFactory, Graph graph) {
        this.engineFactory = engineFactory;
        this.graph = graph;
    }

    public EntityId createCar(CarModel model, String nodeId) {
        Node startNode = graph.getNodes().stream()
                .filter(node -> node.getId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));

        PositionComponent pos = new PositionComponent();
        pos.setSnapshot(new PositionSnapshot(startNode));

        SpeedComponent speed = new SpeedComponent();
        speed.setSnapshot(new SpeedSnapshot(80));

        ColorComponent color = new ColorComponent();
        color.setSnapshot(new ColorSnapshot("#0000FF"));

        EngineType engineType = EngineType.FUEL;
        EngineComponent engineComponent = engineFactory.create();
        engineComponent.setEngine(engineType);

        EnergyStorageComponent storage = new EnergyStorageComponent();
        Map<EngineType, EnergyStorage> energyStorageMap = new ConcurrentHashMap<>();

        for (Engine engine : engineFactory.getEngines()) {
            energyStorageMap.put(engine.getType(), new EnergyStorage(engine.capacity(), engine.capacity()));
        }
        storage.setSnapshot(new EnergyStorageSnapshot(energyStorageMap));

        StateComponent state = new StateComponent();

        MessageComponent msg = new MessageComponent();
        msg.addMessage(MessageType.ALERT, "");
        switch (Objects.requireNonNull(startNode).getType()) {
            case NodeType.WORKSHOP:
                state.setSnapshot(new StateSnapshot(State.WAIT_AT_WORKSHOP));
                msg.addMessage(MessageType.WARNING, "");
                break;
            case NodeType.INTERSECTION:
                state.setSnapshot(new StateSnapshot(State.WAIT_AT_INTERSECTION));
                msg.addMessage(MessageType.WARNING, "Must be at workshop to change engine or color.");
                break;
            case NodeType.GASSTATION:
                state.setSnapshot(new StateSnapshot(State.WAIT_AT_GASSTATION));
                msg.addMessage(MessageType.WARNING, "Must be at workshop to change engine or color.");
                break;
        }

        PathComponent path = new PathComponent();

        EntityId carId = model.createEntity();
        model.getPositions().put(carId, pos);
        model.getStorage().put(carId, storage);
        model.getSpeeds().put(carId, speed);
        model.getColors().put(carId, color);
        model.getEngines().put(carId, engineComponent);
        model.getStates().put(carId, state);
        model.getPaths().put(carId, path);
        model.getMessages().put(carId, msg);

        return carId;
    }
}
