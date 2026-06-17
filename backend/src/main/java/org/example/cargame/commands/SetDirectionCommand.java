package org.example.cargame.commands;

import org.example.cargame.model.CarModel;
import org.example.cargame.Dijkstra;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.MessageType;
import org.example.cargame.enums.NodeType;
import org.example.cargame.enums.State;
import org.example.cargame.graph.Edge;
import org.example.cargame.graph.Graph;
import org.example.cargame.graph.Node;
import org.example.cargame.observer.ObserverDispatcher;
import org.example.cargame.snapshot.PathSnapshot;
import org.example.cargame.snapshot.StateSnapshot;

import java.util.List;

public class SetDirectionCommand implements Command {
    private final CarModel model;
    private final Dijkstra dij;
    private final Graph graph;
    private final ObserverDispatcher dispatcher;

    private final EntityId playerId;
    private final String targetId;

    public SetDirectionCommand(CarModel model,
            Dijkstra dij,
            Graph graph,
            EntityId playerId,
            String targetId,
            ObserverDispatcher dispatcher) {
        this.model = model;
        this.dij = dij;
        this.graph = graph;
        this.playerId = playerId;
        this.targetId = targetId;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        var stateComp = model.getStates().get(playerId);
        if (stateComp == null)
            return;
        if (State.DRIVE == stateComp.getSnapshot().state())
            return;

        String currentId = model.getPositions().get(playerId).getSnapshot().currentNode().getId();
        if (!targetId.equals(currentId)) {
            Node targetNode = graph.getNodeById(targetId);
            Node currentNode = model.getPositions().get(playerId).getSnapshot().currentNode();

            List<Edge> path = dij.calcShortestPath(currentNode, targetNode, graph.getNodes());

            if (path.isEmpty())
                return;

            var engineType = model.getEngines().get(playerId).getSnapshot().activeEngine().getType();
            double currentPower = model.getStorage().get(playerId).getSnapshot().get(engineType).power();
            double capacity = model.getStorage().get(playerId).getSnapshot().get(engineType).capacity();

            for (Edge edge : path) {
                double weight = edge.getWeight();
                if (currentPower < weight) {
                    var compMessages = model.getMessages().get(playerId);
                    compMessages.addMessage(MessageType.ALERT, "Not enough Power to reach target");
                    dispatcher.dispatch(() -> compMessages.notifyObservers(playerId, compMessages.getSnapshot()));
                    return;
                }
                currentPower -= weight;

                Node toNode = edge.getTo();
                if (toNode.getType() == NodeType.GASSTATION) {
                    currentPower = capacity;
                }
            }

            double minDistanceToGas = graph.getNodes().stream()
                    .filter(node -> node.getType() == NodeType.GASSTATION)
                    .mapToDouble(gasNode -> {
                        if (gasNode.equals(targetNode))
                            return 0.0;
                        List<Edge> pathToGas = dij.calcShortestPath(targetNode, gasNode, graph.getNodes());
                        if (pathToGas.isEmpty())
                            return Double.POSITIVE_INFINITY;
                        return pathToGas.stream().mapToDouble(Edge::getWeight).sum();
                    })
                    .min()
                    .orElse(Double.POSITIVE_INFINITY);

            if (currentPower < minDistanceToGas) {
                var compMessages = model.getMessages().get(playerId);
                compMessages.addMessage(MessageType.ALERT,
                        "Not enough Power to reach next gas station after target");
                dispatcher.dispatch(() -> compMessages.notifyObservers(playerId, compMessages.getSnapshot()));
                return;
            }

            PathSnapshot newSnap = new PathSnapshot(path);
            model.getPaths().get(playerId).setSnapshot(newSnap);

            var compState = model.getStates().get(playerId);
            compState.setSnapshot(new StateSnapshot(State.DRIVE));

            var compMessages = model.getMessages().get(playerId);
            compMessages.addMessage(MessageType.ALERT, "");
            compMessages.addMessage(MessageType.WARNING,
                    "Must be at workshop to change engine or color.");

            dispatcher.dispatch(() -> {
                compState.notifyObservers(playerId, compState.getSnapshot());
                compMessages.notifyObservers(playerId, compMessages.getSnapshot());
            });

        }
    }
}
