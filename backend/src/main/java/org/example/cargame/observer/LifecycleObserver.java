package org.example.cargame.observer;

import org.example.cargame.entity.EntityId;

public interface LifecycleObserver {
    void onEntityCreated(EntityId id);
    void onEntityRemoved(EntityId id);
    void onLoad();
}
