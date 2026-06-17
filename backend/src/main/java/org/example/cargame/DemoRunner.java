package org.example.cargame;

import org.example.cargame.model.CarModel;
import org.example.cargame.observer.GameStateView;
import org.example.cargame.observer.ObserverDispatcher;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DemoRunner implements CommandLineRunner, DisposableBean {

    private static final Logger log = Logger.getLogger(DemoRunner.class.getName());

    private final CarModel model;
    private final ObserverDispatcher dispatcher;
    private final CommandQueue commands;
    private final GameStateView gameStateView;

    private Thread gameLoopThread;
    private Thread observerThread;

    public DemoRunner(CarModel model, ObserverDispatcher dispatcher, CommandQueue commands,
            GameStateView gameStateView) {
        this.model = model;
        this.dispatcher = dispatcher;
        this.commands = commands;
        this.gameStateView = gameStateView;
    }

    @Override
    public void run(String @NonNull... args) {
        PhysicsEngine physics = new PhysicsEngine(model, gameStateView, dispatcher);
        GameLoop loop = new GameLoop(physics, commands);

        observerThread = Thread.ofVirtual().name("ObserverThread").start(dispatcher);
        gameLoopThread = Thread.ofVirtual().name("GameLoopThread").start(loop);
    }

    @Override
    public void destroy() {
        log.info("Shutting down game threads...");

        if (gameLoopThread != null) {
            gameLoopThread.interrupt();
        }
        if (observerThread != null) {
            observerThread.interrupt();
        }

        try {
            if (gameLoopThread != null) gameLoopThread.join(2000);
            if (observerThread != null) observerThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Game threads shut down.");
    }
}
