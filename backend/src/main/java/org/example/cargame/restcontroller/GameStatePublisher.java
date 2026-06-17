package org.example.cargame.restcontroller;

import org.example.cargame.CreateRemoveLayer;
import org.example.cargame.SaveLoadLayer;
import org.example.cargame.event.GameLoadedEvent;
import org.example.cargame.snapshot.GameStateDTO;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.example.cargame.event.EntityCreatedEvent;
import org.example.cargame.event.EntityRemovedEvent;
import org.example.cargame.ServiceLayer;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class GameStatePublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final ServiceLayer serviceLayer;
    private final CreateRemoveLayer createRemoveLayer;
    private final SaveLoadLayer saveLoadLayer;

    private final Map<Integer, GameStateDTO> lastPublished = new ConcurrentHashMap<>();
    private volatile long lastHeartbeatTime = System.currentTimeMillis();

    public GameStatePublisher(SimpMessagingTemplate messagingTemplate, ServiceLayer serviceLayer,
            CreateRemoveLayer createRemoveLayer, SaveLoadLayer saveLoadLayer) {
        this.messagingTemplate = messagingTemplate;
        this.serviceLayer = serviceLayer;
        this.createRemoveLayer = createRemoveLayer;
        this.saveLoadLayer = saveLoadLayer;
    }

    @EventListener
    public void handleGameLoaded(GameLoadedEvent event) {
        lastPublished.clear();
        lastHeartbeatTime = 0;
        messagingTemplate.convertAndSend("/topic/game/sync", serviceLayer.getAllGameStates().values());
    }

    @Scheduled(fixedRate = 50)
    public void publishGameState() {
        if (!saveLoadLayer.isLoadingComplete() || createRemoveLayer.getUpdateInProgress()) {
            return;
        }

        Map<Integer, GameStateDTO> snapshot = serviceLayer.getAllGameStates();

        boolean forceHeartbeat = (System.currentTimeMillis() - lastHeartbeatTime) > 2000;

        snapshot.forEach((id, state) -> {
            GameStateDTO lastState = lastPublished.get(id);
            if (forceHeartbeat || !state.equals(lastState)) {
                messagingTemplate.convertAndSend("/topic/game", state);
                lastPublished.put(id, state);
            }
        });

        if (forceHeartbeat) {
            lastHeartbeatTime = System.currentTimeMillis();
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        Map<Integer, GameStateDTO> snapshot = serviceLayer.getAllGameStates();
        snapshot.forEach((id, state) -> {
            messagingTemplate.convertAndSend("/topic/game", state);
            lastPublished.put(id, state);
        });
    }

    @EventListener
    public void handleEntityCreated(EntityCreatedEvent event) {
        GameStateDTO state = serviceLayer.getAllGameStates().get(event.entityId());
        if (state != null) {
            messagingTemplate.convertAndSend("/topic/game", state);
            lastPublished.put(event.entityId(), state);
        }
    }

    @EventListener
    public void handleEntityRemoved(EntityRemovedEvent event) {
        Map<Integer, GameStateDTO> states = serviceLayer.getAllGameStates();
        lastPublished.keySet().retainAll(states.keySet());
        lastPublished.putAll(states);
        messagingTemplate.convertAndSend("/topic/game/sync", states.values());
    }
}
