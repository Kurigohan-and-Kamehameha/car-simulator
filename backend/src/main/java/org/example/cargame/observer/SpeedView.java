package org.example.cargame.observer;

import org.example.cargame.model.HasSpeeds;
import org.example.cargame.entity.EntityId;
import org.example.cargame.snapshot.SpeedSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SpeedView extends ParentView<HasSpeeds> implements PushObserver<SpeedSnapshot> {
    private final Map<EntityId, SpeedSnapshot> cache = new ConcurrentHashMap<>();

    public SpeedView(HasSpeeds model, ObserverDispatcher dispatcher) {
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
        SpeedSnapshot snap = model.getSpeeds().get(id).getSnapshot();
        model.getSpeeds().get(id).addObserver(this);
        dispatcher.dispatch(() -> cache.put(id, snap));
    }

    @Override
    public void rebind() {
        dispatcher.dispatch(cache::clear);
        bind();
    }

    @Override
    public void unbind(EntityId id) {
        model.getSpeeds().get(id).removeObserver(this);
        dispatcher.dispatch(() -> cache.remove(id));
    }

    @Override
    public void update(EntityId id, SpeedSnapshot data) {
        cache.put(id, data);
        super.notifyObservers(id);
    }

    public double getSpeed(EntityId id) {
        SpeedSnapshot snap = cache.get(id);
        return snap != null ? snap.speed() : 0;
    }

}
