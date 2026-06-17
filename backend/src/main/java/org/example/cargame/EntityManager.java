package org.example.cargame;

import org.example.cargame.entity.EntityId;
import org.example.cargame.enums.ModelType;
import org.example.cargame.factories.CarFactory;
import org.example.cargame.model.CarModel;
import org.example.cargame.model.Model;
import org.springframework.stereotype.Service;

@Service
public class EntityManager<T extends Model> {

    private final CarFactory carFactory;

    public EntityManager(CarFactory carFactory) {
        this.carFactory = carFactory;
    }

    public EntityId createEntity(T model, String nodeId) {
        switch (model.getType()) {
            case ModelType.CARMODEL:
                return carFactory.createCar((CarModel) model, nodeId);
        }
        ;
        return null;
    }

    public void removeEntity(T model, EntityId id) {
        switch (model.getType()) {
            case ModelType.CARMODEL:
                model.removeEntity(id);
                break;
        }
    }

}
