package org.example.cargame.model;

import org.example.cargame.components.PositionComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasPositions extends EntityModel {
    Map<EntityId, PositionComponent> getPositions();
}
