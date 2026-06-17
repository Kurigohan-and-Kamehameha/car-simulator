package org.example.cargame;

import org.example.cargame.graph.Graph;
import org.example.cargame.graph.Node;
import org.example.cargame.model.CarModel;
import org.example.cargame.observer.GameStateView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CreateDeleteTest {

    @Autowired
    private ServiceLayer serviceLayer;

    @Autowired
    private CreateRemoveLayer createRemoveLayer;

    @Autowired
    private CommandQueue commands;

    @Autowired
    private CarModel model;

    @Autowired
    private GameStateView gameStateView;

    @BeforeEach
    void resetModel() {
        model.clear();
        gameStateView.clear();
    }

    @Test
    void testCreateDeleteEntity() throws Exception {
        Graph graph = new Graph();
        Node a = new Node("A", 400, 200, null);
        graph.addNode(a);

        Integer newId = createRemoveLayer.createEntity("A").get();

        commands.executeAll();
        List<Integer> entitiesAfterCreate = serviceLayer.getAllGameStates().keySet().stream().toList();

        createRemoveLayer.removeEntity(newId);

        commands.executeAll();

        List<Integer> entitiesAfterRemove = serviceLayer.getAllGameStates().keySet().stream().toList();

        assertEquals(1, entitiesAfterCreate.size());
        assertEquals(0, entitiesAfterRemove.size());
    }

}