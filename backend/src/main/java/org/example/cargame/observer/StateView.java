package org.example.cargame.observer;

import org.example.cargame.model.HasStates;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.State;
import org.example.cargame.snapshot.StateSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StateView extends ParentView<HasStates> implements PushObserver<StateSnapshot> {
    private final Map<EntityId, StateSnapshot> cache = new ConcurrentHashMap<>();

    public StateView(HasStates model, ObserverDispatcher dispatcher) {
        super(model, dispatcher);

        bind();
    }

    @Override
    public void bind() {
        for (EntityId id : model.getAllEntities()) {
            bind(id);
        }
    }

    @Override
    public void bind(EntityId id) {
        StateSnapshot snapshot = model.getStates().get(id).getSnapshot();
        model.getStates().get(id).addObserver(this);
        dispatcher.dispatch(() -> cache.put(id, snapshot));
    }

    @Override
    public void rebind() {
        dispatcher.dispatch(cache::clear);
        bind();
    }

    @Override
    public void unbind(EntityId id) {
        model.getStates().get(id).removeObserver(this);
        dispatcher.dispatch(() -> cache.remove(id));
    }

    @Override
    public void update(EntityId id, StateSnapshot data) {
        cache.put(id, data);
    }

    public State getState(EntityId id) {
        return cache.get(id) != null ? cache.get(id).state() : State.WAIT_AT_WORKSHOP;
    }

}
