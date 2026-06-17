package org.example.cargame;

import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.NodeType;
import org.example.cargame.enums.State;
import org.example.cargame.graph.Edge;
import org.example.cargame.graph.Node;
import org.example.cargame.model.CarModel;
import org.example.cargame.observer.GameStateView;
import org.example.cargame.observer.ObserverDispatcher;
import org.example.cargame.snapshot.*;
import org.example.cargame.enums.MessageType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicsEngine {

    private final CarModel model;
    private final GameStateView gameStateView;
    private final ObserverDispatcher dispatcher;
    private static final double DELTA_TIME = 0.016;
    private final Map<EntityId, State> lastSentStates = new HashMap<>();

    public PhysicsEngine(CarModel model, GameStateView gameStateView, ObserverDispatcher dispatcher) {
        this.model = model;
        this.gameStateView = gameStateView;
        this.dispatcher = dispatcher;
    }

    public void update() {
        for (EntityId id : model.getAllEntities()) {
            if (model.getStates().get(id).getSnapshot().state() != State.DRIVE) {
                continue;
            }
            PositionSnapshot currentPosSnap = model.getPositions().get(id).getSnapshot();
            PathSnapshot pathSnap = model.getPaths().get(id).getSnapshot();
            if (pathSnap == null || pathSnap.path().isEmpty())
                continue;

            int currentIndex = pathSnap.getCurrentEdgeIndex();
            List<Edge> path = pathSnap.path();

            Edge edge;
            double progress;

            if (currentPosSnap.currentEdge() != null) {
                edge = currentPosSnap.currentEdge();
                progress = currentPosSnap.edgeProgress();
            } else {
                edge = path.get(currentIndex);
                progress = 0.0;
            }

            SpeedSnapshot currentSpeedSnap = model.getSpeeds().get(id).getSnapshot();
            double speed = currentSpeedSnap.speed();
            double potentialDelta = speed / edge.getWeight() * DELTA_TIME;

            double remainingOnEdge = 1.0 - progress;
            double actualDelta = Math.min(potentialDelta, remainingOnEdge);
            double newProgress = progress + actualDelta;
            double distance = actualDelta * edge.getWeight();

            var engineType = model.getEngines().get(id).getSnapshot().activeEngine().getType();
            model.getStorage().get(id).consume(engineType, distance);

            if (newProgress >= 1.0) {
                currentIndex++;
                if (currentIndex < path.size()) {
                    edge = path.get(currentIndex);
                    if (edge.getFrom().getType().equals(NodeType.GASSTATION)) {
                        model.getStorage().get(id).refill(engineType);
                    }
                    newProgress = 0.0;

                    pathSnap = new PathSnapshot(path, currentIndex);
                    model.getPaths().get(id).setSnapshot(pathSnap);

                } else {
                    Node target = edge.getTo();
                    model.getPositions().get(id).setSnapshot(new PositionSnapshot(target));

                    switch (target.getType()) {
                        case WORKSHOP -> {
                            model.getStates().get(id).setSnapshot(new StateSnapshot(State.WAIT_AT_WORKSHOP));
                            model.getMessages().get(id).addMessage(MessageType.WARNING, "");
                        }
                        case INTERSECTION ->
                            model.getStates().get(id).setSnapshot(new StateSnapshot(State.WAIT_AT_INTERSECTION));
                        case GASSTATION -> {
                            model.getStates().get(id).setSnapshot(new StateSnapshot(State.WAIT_AT_GASSTATION));
                            model.getStorage().get(id).refill(engineType);
                        }
                    }
                    continue;
                }
            }
            double x = toX(newProgress, edge);
            double y = toY(newProgress, edge);
            model.getPositions().get(id).setSnapshot(new PositionSnapshot(null, edge, newProgress, x, y));

        }
    }

    private double toX(double edgeProgress, Edge edge) {
        return edge.getFrom().getX() +
                (edge.getTo().getX() - edge.getFrom().getX()) * edgeProgress;
    }

    private double toY(double edgeProgress, Edge edge) {
        return edge.getFrom().getY() +
                (edge.getTo().getY() - edge.getFrom().getY()) * edgeProgress;
    }

    public void notifyObservers() {
        lastSentStates.keySet().retainAll(model.getAllEntities());

        for (EntityId id : model.getAllEntities()) {
            State currentState = model.getStates().get(id).getSnapshot().state();
            State lastState = lastSentStates.get(id);

            if (currentState == State.DRIVE || currentState != lastState) {

                var posComp = model.getPositions().get(id);
                var stateComp = model.getStates().get(id);
                var messageComp = model.getMessages().get(id);
                var storageComp = model.getStorage().get(id);

                var posSnap = posComp.getSnapshot();
                var stateSnap = stateComp.getSnapshot();
                var messageSnap = messageComp.getSnapshot();
                var storageSnap = storageComp.getSnapshot();

                dispatcher.dispatch(() -> {
                    posComp.notifyObservers(id, posSnap);
                    stateComp.notifyObservers(id, stateSnap);
                    messageComp.notifyObservers(id, messageSnap);
                    storageComp.notifyObservers(id, storageSnap);

                    gameStateView.update(id);
                });

                lastSentStates.put(id, currentState);
            }
        }
    }

}
