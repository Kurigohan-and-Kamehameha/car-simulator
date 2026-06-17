package org.example.cargame;

import org.example.cargame.entity.EntityId;
import org.example.cargame.event.EntityCreatedEvent;
import org.example.cargame.event.EntityRemovedEvent;
import org.example.cargame.event.GameLoadedEvent;
import org.example.cargame.observer.GameStateView;
import org.example.cargame.observer.LifecycleObserver;
import org.example.cargame.observer.ObserverDispatcher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class LifecycleService {
    private final GameStateView gameStateView;
    private final ApplicationEventPublisher eventPublisher;
    private final ObserverDispatcher dispatcher;
    private final List<LifecycleObserver> lifecycleObservers;

    public LifecycleService(GameStateView gameStateView,
            ApplicationEventPublisher eventPublisher,
            ObserverDispatcher dispatcher, List<LifecycleObserver> lifecycleObservers) {
        this.gameStateView = gameStateView;
        this.eventPublisher = eventPublisher;
        this.dispatcher = dispatcher;
        this.lifecycleObservers = lifecycleObservers;
    }

    public void onEntityCreated(EntityId id, CompletableFuture<Integer> future, Runnable onComplete) {
        lifecycleObservers.forEach(o -> o.onEntityCreated(id));
        dispatcher.dispatch(() -> {
            gameStateView.update(id);
            eventPublisher.publishEvent(new EntityCreatedEvent(id.getId()));
            future.complete(id.getId());
            onComplete.run();
        });
    }

    public void onEntityRemoved(EntityId id, CompletableFuture<Void> future, Runnable onComplete) {
        lifecycleObservers.forEach(o -> o.onEntityRemoved(id));
        dispatcher.dispatch(() -> {
            gameStateView.remove(id);
            eventPublisher.publishEvent(new EntityRemovedEvent(id.getId()));
            future.complete(null);
            onComplete.run();
        });
    }

    public void onLoad(List<Integer> list) {
        lifecycleObservers.forEach(LifecycleObserver::onLoad);
        dispatcher.dispatch(() -> {
            gameStateView.clear();
            for (Integer id : list) {
                gameStateView.update(new EntityId(id));
            }
            eventPublisher.publishEvent(new GameLoadedEvent());
        });
    }
}
