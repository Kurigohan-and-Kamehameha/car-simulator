package org.example.cargame.model;

import org.example.cargame.components.EngineComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasEngines extends EntityModel {
    Map<EntityId, EngineComponent> getEngines();
}
