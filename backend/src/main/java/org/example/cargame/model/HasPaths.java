package org.example.cargame.model;

import org.example.cargame.components.PathComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasPaths extends EntityModel {
    Map<EntityId, PathComponent> getPaths();
}
