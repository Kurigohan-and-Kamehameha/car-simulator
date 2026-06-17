package org.example.cargame.observer;

import org.example.cargame.model.HasMessages;
import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.MessageType;
import org.example.cargame.snapshot.MessageSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageView extends ParentView<HasMessages> implements PushObserver<MessageSnapshot> {
    private final Map<EntityId, MessageSnapshot> cache = new ConcurrentHashMap<>();

    public MessageView(HasMessages model, ObserverDispatcher dispatcher) {
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
        MessageSnapshot snapshot = model.getMessages().get(id).getSnapshot();
        model.getMessages().get(id).addObserver(this);
        dispatcher.dispatch(() -> cache.put(id, snapshot));
    }

    @Override
    public void rebind() {
        dispatcher.dispatch(cache::clear);
        bind();
    }

    @Override
    public void unbind(EntityId id) {
        model.getMessages().get(id).removeObserver(this);
        dispatcher.dispatch(() -> cache.remove(id));
    }

    @Override
    public void update(EntityId id, MessageSnapshot data) {
        MessageSnapshot old = cache.put(id, data);
        if (old == null || !old.equals(data)) {
            super.notifyObservers(id);
        }
    }

    public String alert(EntityId id) {
        MessageSnapshot snap = cache.get(id);
        return snap != null && snap.messages() != null ? snap.messages().getOrDefault(MessageType.ALERT, "") : "";
    }

    public String warning(EntityId id) {
        MessageSnapshot snap = cache.get(id);
        return snap != null && snap.messages() != null ? snap.messages().getOrDefault(MessageType.WARNING, "") : "";
    }

}
