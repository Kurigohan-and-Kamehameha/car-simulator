package org.example.cargame.observer;

import org.example.cargame.model.HasColors;
import org.example.cargame.entity.EntityId;
import org.example.cargame.snapshot.ColorSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ColorView extends ParentView<HasColors> implements PushObserver<ColorSnapshot> {
    private final Map<EntityId, ColorSnapshot> cache = new ConcurrentHashMap<>();

    public ColorView(HasColors model, ObserverDispatcher dispatcher) {
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
        ColorSnapshot color = model.getColors().get(id).getSnapshot();
        model.getColors().get(id).addObserver(this);
        dispatcher.dispatch(() -> cache.put(id, color));
    }

    @Override
    public void rebind() {
        dispatcher.dispatch(cache::clear);
        bind();
    }

    @Override
    public void unbind(EntityId id) {
        model.getColors().get(id).removeObserver(this);
        dispatcher.dispatch(() -> cache.remove(id));
    }

    @Override
    public void update(EntityId id, ColorSnapshot data) {
        cache.put(id, data);
        super.notifyObservers(id);
    }

    public String getColor(EntityId id) {
        return cache.get(id) != null ? cache.get(id).color() : "#000000";
    }

}
