package org.example.cargame.observer;

import org.example.cargame.model.HasPositions;
import org.example.cargame.entity.EntityId;
import org.example.cargame.snapshot.PositionSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PositionView extends ParentView<HasPositions> implements PushObserver<PositionSnapshot> {
    private final Map<EntityId, PositionSnapshot> cache = new ConcurrentHashMap<>();

    public PositionView(HasPositions model, ObserverDispatcher dispatcher) {
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
        PositionSnapshot snap = model.getPositions().get(id).getSnapshot();
        model.getPositions().get(id).addObserver(this);
        dispatcher.dispatch(() -> cache.put(id, snap));
    }

    @Override
    public void rebind() {
        dispatcher.dispatch(cache::clear);
        bind();
    }

    @Override
    public void unbind(EntityId id) {
        model.getPositions().get(id).removeObserver(this);
        dispatcher.dispatch(() -> cache.remove(id));
    }

    @Override
    public void update(EntityId id, PositionSnapshot data) {
        cache.put(id, data);
    }

    public PositionSnapshot getPosition(EntityId id) {
        return cache.get(id);
    }

}