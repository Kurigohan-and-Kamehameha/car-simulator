package org.example.cargame.restcontroller;

import org.example.cargame.SaveLoadLayer;
import org.example.cargame.enums.EngineType;
import org.example.cargame.CreateRemoveLayer;
import org.example.cargame.ServiceLayer;
import org.example.cargame.graph.Graph;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@CrossOrigin
@RequestMapping("/api/game")
public class GameRestController {
    private final ServiceLayer serviceLayer;
    private final CreateRemoveLayer createRemoveLayer;
    private final SaveLoadLayer saveLoadLayer;

    public GameRestController(ServiceLayer serviceLayer, CreateRemoveLayer createRemoveLayer,
            SaveLoadLayer saveLoadLayer) {
        this.serviceLayer = serviceLayer;
        this.createRemoveLayer = createRemoveLayer;
        this.saveLoadLayer = saveLoadLayer;
    }

    @PostMapping("/direction")
    public void setDirection(@RequestParam String nodeId, @RequestParam Integer id) {
        serviceLayer.setDirection(nodeId, id);
    }

    @PostMapping("/color")
    public void setColor(@RequestParam String color, @RequestParam Integer id) {
        serviceLayer.setColor(color, id);
    }

    @PostMapping("/engine")
    public void setEngine(@RequestParam EngineType engineType, @RequestParam Integer id) {
        serviceLayer.setEngine(engineType, id);
    }

    @PostMapping("/speed")
    public void setSpeed(@RequestParam double speed, @RequestParam Integer id) {
        serviceLayer.setSpeed(speed, id);
    }

    @PostMapping("/save")
    public void save(@RequestParam String path) {
        saveLoadLayer.save();
    }

    @PostMapping("/load")
    public void load(@RequestParam String path) {
        saveLoadLayer.load();
    }

    @PostMapping("/createEntity")
    public Integer createEntity(@RequestParam String nodeId) throws Exception {
        return createRemoveLayer.createEntity(nodeId).get();
    }

    @DeleteMapping("/removeEntity")
    public void removeEntity(@RequestParam int id) {
        createRemoveLayer.removeEntity(id);
    }

    @GetMapping("/graph")
    public java.util.Map<String, Object> getGraph() {
        Graph graph = serviceLayer.getGraph();
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        java.util.List<java.util.Map<String, Object>> nodes = new java.util.ArrayList<>();
        for (org.example.cargame.graph.Node n : graph.getNodes()) {
            nodes.add(java.util.Map.of("id", n.getId(), "x", n.getX(), "y", n.getY(), "type", n.getType().toString()));
        }
        java.util.List<java.util.Map<String, Object>> edges = new java.util.ArrayList<>();
        for (org.example.cargame.graph.Edge e : graph.getEdges()) {
            edges.add(java.util.Map.of("source", e.getFrom().getId(), "target", e.getTo().getId()));
        }
        result.put("nodes", nodes);
        result.put("edges", edges);
        return result;
    }

}
