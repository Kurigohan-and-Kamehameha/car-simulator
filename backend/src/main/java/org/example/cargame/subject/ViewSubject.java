package org.example.cargame.subject;

import org.example.cargame.entity.EntityId;
import org.example.cargame.observer.Observer;

public class ViewSubject extends Subject<Observer> {

    public void notifyObservers(EntityId id) {
        observers.forEach(o -> o.update(id));
    }

}
