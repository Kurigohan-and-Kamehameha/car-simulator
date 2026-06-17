package org.example.cargame.persistence;

import org.example.cargame.model.CarModel;
import org.example.cargame.components.*;
import org.example.cargame.entity.EntityId;
import org.example.cargame.factories.EngineFactory;
import org.example.cargame.snapshot.ColorSnapshot;
import org.example.cargame.snapshot.SpeedSnapshot;
import org.example.cargame.snapshot.MessageSnapshot;
import org.example.cargame.snapshot.StateSnapshot;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class GameLoader {

    public void apply(LoadedGameData data, CarModel model, EngineFactory engineFactory) {
        model.clear();

        Set<EntityId> allIds = new HashSet<>();
        allIds.addAll(data.positions().keySet());
        allIds.addAll(data.speeds().keySet());
        allIds.addAll(data.colors().keySet());
        allIds.addAll(data.engines().keySet());
        allIds.addAll(data.paths().keySet());
        allIds.addAll(data.messages().keySet());
        allIds.addAll(data.storage().keySet());
        allIds.addAll(data.states().keySet());

        allIds.forEach(id -> {
            if (data.speeds().containsKey(id)) {
                SpeedComponent speedComp = new SpeedComponent();
                speedComp.setSnapshot(new SpeedSnapshot(data.speeds().get(id)));
                model.getSpeeds().put(id, speedComp);
            }

            if (data.positions().containsKey(id)) {
                PositionComponent posComp = new PositionComponent();
                posComp.setSnapshot(data.positions().get(id));
                model.getPositions().put(id, posComp);
            }

            if (data.colors().containsKey(id)) {
                ColorComponent colorComp = new ColorComponent();
                colorComp.setSnapshot(new ColorSnapshot(data.colors().get(id)));
                model.getColors().put(id, colorComp);
            }

            if (data.engines().containsKey(id)) {
                EngineComponent engineComp = engineFactory.create();
                engineComp.setEngine(data.engines().get(id));
                model.getEngines().put(id, engineComp);
            }

            if (data.paths().containsKey(id)) {
                PathComponent pathComp = new PathComponent();
                pathComp.setSnapshot(data.paths().get(id));
                model.getPaths().put(id, pathComp);
            }

            if (data.messages().containsKey(id)) {
                MessageComponent msgComp = new MessageComponent();
                msgComp.setSnapshot(new MessageSnapshot(data.messages().get(id)));
                model.getMessages().put(id, msgComp);
            }

            if (data.storage().containsKey(id)) {
                EnergyStorageComponent storageComp = new EnergyStorageComponent();
                storageComp.setSnapshot(data.storage().get(id));
                model.getStorage().put(id, storageComp);
            }

            if (data.states().containsKey(id)) {
                StateComponent stateComp = new StateComponent();
                stateComp.setSnapshot(new StateSnapshot(data.states().get(id)));
                model.getStates().put(id, stateComp);
            }
        });

        int maxId = allIds.stream()
                .mapToInt(EntityId::getId)
                .max()
                .orElse(0);

        model.resetNextId(maxId + 1);
    }

}
