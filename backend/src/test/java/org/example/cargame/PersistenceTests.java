package org.example.cargame;

import org.example.cargame.components.ColorComponent;
import org.example.cargame.components.SpeedComponent;
import org.example.cargame.components.StateComponent;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.State;
import org.example.cargame.factories.EngineFactory;
import org.example.cargame.model.CarModel;
import org.example.cargame.persistence.GameLoader;
import org.example.cargame.persistence.LoadedGameData;
import org.example.cargame.persistence.PersistenceLayerDataBase;
import org.example.cargame.persistence.SnapshotBuilder;
import org.example.cargame.snapshot.ColorSnapshot;
import org.example.cargame.snapshot.SpeedSnapshot;
import org.example.cargame.snapshot.StateSnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PersistenceTests {

    @Autowired
    private PersistenceLayerDataBase persistence;

    @Autowired
    private CarModel model;

    @Autowired
    private EngineFactory engineFactory;

    @Test
    void testSaveAndLoad() {

        EntityId id = new EntityId(1);

        ColorComponent color = new ColorComponent();
        color.setSnapshot(new ColorSnapshot("red"));
        model.getColors().put(id, color);

        SpeedComponent speed = new SpeedComponent();
        speed.setSnapshot(new SpeedSnapshot(42));
        model.getSpeeds().put(id, speed);

        StateComponent state = new StateComponent();
        state.setSnapshot(new StateSnapshot(State.WAIT_AT_WORKSHOP));
        model.getStates().put(id, state);

        SnapshotBuilder builder = new SnapshotBuilder();
        LoadedGameData data = builder.build(model);

        persistence.save(data);
        LoadedGameData loaded = persistence.load();

        GameLoader loader = new GameLoader();

        CarModel newModel = new CarModel();
        loader.apply(loaded, newModel, engineFactory);

        assertEquals("red", newModel.getColors().get(id).getSnapshot().color());
        assertEquals(42, newModel.getSpeeds().get(id).getSnapshot().speed());
        assertEquals(State.WAIT_AT_WORKSHOP, newModel.getStates().get(id).getSnapshot().state());
    }
}