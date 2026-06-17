package org.example.cargame;

import org.example.cargame.entity.EntityId;
import org.example.cargame.factories.EngineFactory;
import org.example.cargame.model.CarModel;
import org.example.cargame.observer.ObserverDispatcher;
import org.example.cargame.persistence.GameLoader;
import org.example.cargame.persistence.LoadedGameData;
import org.example.cargame.persistence.PersistenceLayerDataBase;
import org.example.cargame.persistence.SnapshotBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SaveLoadLayer {
    private final CarModel model;
    private final CommandQueue commands;
    private final LifecycleService lifecycleService;
    private final ObserverDispatcher dispatcher;
    private final UpdateState updateState;
    private final EngineFactory engineFactory;
    private final PersistenceLayerDataBase persistenceLayerDataBase;
    private final GameLoader loader;

    public SaveLoadLayer(CarModel model, CommandQueue commands, LifecycleService lifecycleService,
            ObserverDispatcher dispatcher, UpdateState updateState, EngineFactory engineFactory,
            PersistenceLayerDataBase persistenceLayerDataBase, GameLoader loader) {
        this.model = model;
        this.commands = commands;
        this.lifecycleService = lifecycleService;
        this.dispatcher = dispatcher;
        this.updateState = updateState;
        this.engineFactory = engineFactory;
        this.persistenceLayerDataBase = persistenceLayerDataBase;
        this.loader = loader;
    }

    public void save() {
        commands.submit(() -> {
            SnapshotBuilder snapshotBuilder = new SnapshotBuilder();
            LoadedGameData data = snapshotBuilder.build(model);
            CompletableFuture.runAsync(() -> persistenceLayerDataBase.save(data));
        });
    }

    public void load() {
        if (!updateState.startLoading())
            return;

        LoadedGameData data = persistenceLayerDataBase.load();

        commands.submit(() -> {
            try {
                if (data == null) {
                    updateState.setLoadingComplete(true);
                    return;
                }
                loader.apply(data, model, engineFactory);
                lifecycleService.onLoad(model.getAllEntities().stream().map(EntityId::getId).toList());
            } finally {
                dispatcher.dispatch(() -> {
                    updateState.setLoadingComplete(true);
                });
            }
        });
    }

    public boolean isLoadingComplete() {
        return updateState.isLoadingComplete();
    }

}
