package org.example.cargame.model;

import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.ModelType;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Model {
    protected ModelType type;
    protected final AtomicInteger nextId = new AtomicInteger();

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
