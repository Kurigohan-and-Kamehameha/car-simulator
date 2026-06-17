package org.example.cargame;

import org.example.cargame.graph.Edge;
import org.example.cargame.graph.Node;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class Dijkstra {

    public List<Edge> calcShortestPath(Node start, Node target, Collection<Node> allNodes) {
        Map<Node, Double> dist = new HashMap<>();
        Map<Node, Edge> prevEdge = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));

        for (Node node : allNodes) {
            dist.put(node, Double.POSITIVE_INFINITY);
        }
        dist.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current))
                continue;
            visited.add(current);

            if (current.equals(target))
                break;

            for (Edge edge : current.getEdges()) {
                Node neighbor = edge.getFrom().equals(current) ? edge.getTo() : edge.getFrom();

                if (visited.contains(neighbor))
                    continue;

                double alt = dist.get(current) + edge.getWeight();
                if (alt < dist.get(neighbor)) {
                    dist.put(neighbor, alt);
                    prevEdge.put(neighbor, edge);
                    pq.add(neighbor);
                }
            }
        }

        List<Edge> path = new ArrayList<>();
        Node current = target;

        while (!current.equals(start)) {
            Edge edge = prevEdge.get(current);
            if (edge == null) {
                return Collections.emptyList();
            }

            Node previous = edge.getFrom().equals(current) ? edge.getTo() : edge.getFrom();
            if (!edge.getFrom().equals(previous)) {
                edge = new Edge(previous, current);
            }

            path.add(edge);
            current = previous;
        }

        Collections.reverse(path);

        return path;
    }

    public double minDistanceToNearest(Node source, Predicate<Node> filter, Collection<Node> allNodes) {
        Map<Node, Double> dist = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));

        for (Node node : allNodes) {
            dist.put(node, Double.POSITIVE_INFINITY);
        }
        dist.put(source, 0.0);
        pq.add(source);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current))
                continue;
            visited.add(current);

            for (Edge edge : current.getEdges()) {
                Node neighbor = edge.getFrom().equals(current) ? edge.getTo() : edge.getFrom();
                if (visited.contains(neighbor))
                    continue;
                double alt = dist.get(current) + edge.getWeight();
                if (alt < dist.get(neighbor)) {
                    dist.put(neighbor, alt);
                    pq.add(neighbor);
                }
            }
        }

        return allNodes.stream()
                .filter(filter)
                .mapToDouble(n -> dist.getOrDefault(n, Double.POSITIVE_INFINITY))
                .min()
                .orElse(Double.POSITIVE_INFINITY);
    }
}
