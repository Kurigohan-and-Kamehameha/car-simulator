package org.example.cargame;

import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.ModelType;
import org.example.cargame.observer.LifecycleObserver;
import org.example.cargame.observer.ObserverDispatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Model {
    protected ModelType type;
    protected final AtomicInteger nextId = new AtomicInteger();
    private final List<LifecycleObserver> lifecycleObservers = new ArrayList<>();
    private ObserverDispatcher dispatcher;

    public void setDispatcher(ObserverDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void addLifecycleObserver(LifecycleObserver observer) {
        lifecycleObservers.add(observer);
    }

    public void removeLifecycleObserver(LifecycleObserver observer) {
        lifecycleObservers.remove(observer);
    }

    public void notifyCreated(EntityId id) {
        dispatcher.dispatch(() -> lifecycleObservers.forEach(o -> o.onEntityCreated(id)));
    }

    public void notifyRemoved(EntityId id) {
        dispatcher.dispatch(() -> lifecycleObservers.forEach(o -> o.onEntityRemoved(id)));
    }

    public EntityId createEntity() {
        return new EntityId(nextId.getAndIncrement());
    }

    public void resetNextId(int value) {
        nextId.set(value);
    }

    public abstract void clear();

    public abstract void removeEntity(EntityId id);

    public abstract ModelType getType();

}
