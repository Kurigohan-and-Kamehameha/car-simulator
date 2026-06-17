package org.example.cargame.observer;

import org.example.cargame.entity.EntityId;
import org.example.cargame.subject.ViewSubject;
import org.springframework.stereotype.Component;

@Component
public abstract class ParentView<T> extends ViewSubject implements Bindable, LifecycleObserver {
    protected final T model;
    protected final ObserverDispatcher dispatcher;

    protected ParentView(T model, ObserverDispatcher dispatcher) {
        this.model = model;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onEntityCreated(EntityId id) {
        bind(id);
    }

    @Override
    public void onEntityRemoved(EntityId id) {
        unbind(id);
    }

    @Override
    public void onLoad() {
        rebind();
    }

}
