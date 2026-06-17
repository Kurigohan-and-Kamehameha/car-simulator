package org.example.cargame;

import java.util.concurrent.locks.LockSupport;

public class GameLoop implements Runnable {
    private final PhysicsEngine physics;
    private final CommandQueue commands;

    public GameLoop(PhysicsEngine physics, CommandQueue commands) {
        this.physics = physics;
        this.commands = commands;
    }

    @Override
    public void run() {
        long tickDelayMs = 16;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                long start = System.currentTimeMillis();

                commands.executeAll();
                physics.update();
                physics.notifyObservers();

                long elapsed = System.currentTimeMillis() - start;
                long sleepTime = tickDelayMs - elapsed;
                if (sleepTime > 0) {
                    LockSupport.parkNanos(sleepTime * 1_000_000);
                } else {
                    Thread.yield();
                }
            } catch (Exception e) {
                System.err.println("Exception in GameLoop:");
                e.printStackTrace();
            }
        }
    }
}
