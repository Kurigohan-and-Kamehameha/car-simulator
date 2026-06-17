package org.example.cargame.observer;

import org.example.cargame.model.HasEngines;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.EngineType;
import org.example.cargame.snapshot.EngineSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EngineView extends ParentView<HasEngines> implements PushObserver<EngineSnapshot> {
    private final Map<EntityId, EngineSnapshot> cache = new ConcurrentHashMap<>();

    public EngineView(HasEngines model, ObserverDispatcher dispatcher) {
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
        EngineSnapshot snapshot = model.getEngines().get(id).getSnapshot();
        model.getEngines().get(id).addObserver(this);
        dispatcher.dispatch(() -> cache.put(id, snapshot));
    }

    @Override
    public void rebind() {
        dispatcher.dispatch(cache::clear);
        bind();
    }

    @Override
    public void unbind(EntityId id) {
        model.getEngines().get(id).removeObserver(this);
        dispatcher.dispatch(() -> cache.remove(id));
    }

    @Override
    public void update(EntityId id, EngineSnapshot data) {
        cache.put(id, data);
        super.notifyObservers(id);
    }

    public EngineType getEngineType(EntityId id) {
        EngineSnapshot snap = cache.get(id);
        return snap != null ? snap.activeEngine().getType() : null;
    }

}
