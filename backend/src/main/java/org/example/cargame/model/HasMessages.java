package org.example.cargame.model;

import org.example.cargame.components.MessageComponent;
import org.example.cargame.entity.EntityId;
import java.util.Map;

public interface HasMessages extends EntityModel {
    Map<EntityId, MessageComponent> getMessages();
}
