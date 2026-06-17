package org.example.cargame;

import org.example.cargame.commands.SetDirectionCommand;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.EngineType;
import org.example.cargame.enums.State;
import org.example.cargame.graph.Graph;
import org.example.cargame.model.CarModel;
import org.example.cargame.observer.*;
import org.example.cargame.snapshot.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceLayer {
    private final CarModel model;
    private final CommandQueue commands;
    private final Dijkstra dij;
    private final Graph graph;
    private final GameStateView gameStateView;
    private final ObserverDispatcher dispatcher;

    public ServiceLayer(CarModel model, CommandQueue commands, Dijkstra dij, Graph graph,
            GameStateView gameStateView,
            ObserverDispatcher dispatcher) {
        this.commands = commands;
        this.model = model;
        this.dij = dij;
        this.graph = graph;
        this.gameStateView = gameStateView;
        this.dispatcher = dispatcher;
    }

    public void setDirection(String targetId, int id) {
        commands.submit(new SetDirectionCommand(
                model, dij, graph, new EntityId(id), targetId, dispatcher));
    }

    public void setColor(String color, int id) {
        commands.submit(() -> {
            if (State.WAIT_AT_WORKSHOP == model.getStates().get(new EntityId(id)).getSnapshot().state()) {
                var comp = model.getColors().get(new EntityId(id));
                comp.setSnapshot(new ColorSnapshot(color));

                dispatcher.dispatch(() -> comp.notifyObservers(new EntityId(id), comp.getSnapshot()));
            }
        });
    }

    public void setSpeed(double speed, int id) {
        commands.submit(() -> {
            var comp = model.getSpeeds().get(new EntityId(id));
            comp.setSnapshot(new SpeedSnapshot(speed));

            dispatcher.dispatch(() -> comp.notifyObservers(new EntityId(id), comp.getSnapshot()));
        });
    }

    public void setEngine(EngineType engineType, int id) {
        commands.submit(() -> {
            if (State.WAIT_AT_WORKSHOP == model.getStates().get(new EntityId(id)).getSnapshot().state()) {
                var engineComponent = model.getEngines().get(new EntityId(id));
                engineComponent.setEngine(engineType);

                dispatcher.dispatch(
                        () -> engineComponent.notifyObservers(new EntityId(id), engineComponent.getSnapshot()));
            }
        });
    }

    public Graph getGraph() {
        return this.graph;
    }

    public List<Integer> getAllEntities() {
        return model.getAllEntities().stream()
                .map(EntityId::getId)
                .toList();
    }

    public Map<Integer, GameStateDTO> getAllGameStates() {
        return gameStateView.getAll().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getId(),
                        Map.Entry::getValue));
    }
}
