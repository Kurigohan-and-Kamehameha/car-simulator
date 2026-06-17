package org.example.cargame.model;

import org.example.cargame.components.StateComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasStates extends EntityModel {
    Map<EntityId, StateComponent> getStates();
}
