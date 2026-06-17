package org.example.cargame.model;

import org.example.cargame.components.EnergyStorageComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasStorage extends EntityModel {
    Map<EntityId, EnergyStorageComponent> getStorage();
}
