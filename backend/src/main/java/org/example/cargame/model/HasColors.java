package org.example.cargame.model;

import org.example.cargame.components.ColorComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasColors extends EntityModel {
    Map<EntityId, ColorComponent> getColors();
}
