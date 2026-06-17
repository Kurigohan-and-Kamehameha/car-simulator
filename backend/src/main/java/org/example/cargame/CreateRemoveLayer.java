package org.example.cargame;

import org.example.cargame.entity.EntityId;
import org.example.cargame.model.CarModel;
import org.example.cargame.model.Model;
import org.example.cargame.observer.ObserverDispatcher;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CreateRemoveLayer {
    private final CarModel model;
    private final CommandQueue commands;
    private final EntityManager<Model> entityManager;
    private final ObserverDispatcher dispatcher;
    private final UpdateState updateState;
    private final LifecycleService lifecycleService;

    public CreateRemoveLayer(CarModel model, CommandQueue commands,
            EntityManager<Model> entityManager,
            ObserverDispatcher dispatcher,
            UpdateState updateState, LifecycleService lifecycleService) {
        this.model = model;
        this.commands = commands;
        this.entityManager = entityManager;
        this.dispatcher = dispatcher;
        this.updateState = updateState;
        this.lifecycleService = lifecycleService;
    }

    public CompletableFuture<Integer> createEntity(String nodeId) {
        updateState.incrementPending();
        CompletableFuture<Integer> future = new CompletableFuture<>();
        boolean submitted = commands.submit(() -> {
            try {
                EntityId entityId = entityManager.createEntity(model, nodeId);
                lifecycleService.onEntityCreated(entityId, future, updateState::decrementPending);
            } catch (Exception e) {
                future.completeExceptionally(e);
                dispatcher.dispatch(updateState::decrementPending);
            }
        });
        if (!submitted) {
            updateState.decrementPending();
            future.completeExceptionally(new IllegalStateException("Server busy, try again"));
        }
        return future;
    }

    public CompletableFuture<Void> removeEntity(int id) {
        updateState.incrementPending();
        CompletableFuture<Void> future = new CompletableFuture<>();
        boolean submitted = commands.submit(() -> {
            try {
                EntityId entityId = new EntityId(id);
                lifecycleService.onEntityRemoved(entityId, future, updateState::decrementPending);
                entityManager.removeEntity(model, entityId);
            } catch (Exception e) {
                future.completeExceptionally(e);
                dispatcher.dispatch(updateState::decrementPending);
            }
        });
        if (!submitted) {
            updateState.decrementPending();
            future.completeExceptionally(new IllegalStateException("Server busy, try again"));
        }
        return future;
    }

    public boolean getUpdateInProgress() {
        return updateState.isUpdateInProgress();
    }
}