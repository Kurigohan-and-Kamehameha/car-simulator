package org.example.cargame.persistence;

import org.example.cargame.model.CarModel;

public final class SnapshotBuilder {

    public LoadedGameData build(CarModel model) {
        LoadedGameData data = new LoadedGameData();

        model.getSpeeds().forEach((id, comp) -> {
            if (comp != null)
                data.speeds().put(id, comp.getSnapshot().speed());
        });

        model.getPositions().forEach((id, comp) -> {
            if (comp != null)
                data.positions().put(id, comp.getSnapshot());
        });

        model.getColors().forEach((id, comp) -> {
            if (comp != null)
                data.colors().put(id, comp.getSnapshot().color());
        });

        model.getEngines().forEach((id, comp) -> {
            if (comp != null)
                data.engines().put(id, comp.getSnapshot().activeEngine().getType());
        });

        model.getMessages().forEach((id, comp) -> {
            if (comp != null)
                data.messages().put(id, comp.getSnapshot().messages());
        });

        model.getStorage().forEach((id, comp) -> {
            if (comp != null)
                data.storage().put(id, comp.getSnapshot());
        });

        model.getStates().forEach((id, comp) -> {
            if (comp != null)
                data.states().put(id, comp.getSnapshot().state());
        });

        model.getPaths().forEach((id, comp) -> {
            if (comp != null)
                data.paths().put(id, comp.getSnapshot());
        });

        return data;
    }
}
