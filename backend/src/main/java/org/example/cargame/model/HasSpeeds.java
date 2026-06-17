package org.example.cargame.model;

import org.example.cargame.components.SpeedComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasSpeeds extends EntityModel {
    Map<EntityId, SpeedComponent> getSpeeds();
}
