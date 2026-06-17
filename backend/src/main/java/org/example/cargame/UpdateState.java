package org.example.cargame;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UpdateState {
    private final AtomicInteger pendingUpdates = new AtomicInteger(0);
    private final AtomicBoolean loadingCompleted = new AtomicBoolean(true);

    public void incrementPending() {
        pendingUpdates.incrementAndGet();
    }

    public void decrementPending() {
        pendingUpdates.decrementAndGet();
    }

    public boolean isUpdateInProgress() {
        return pendingUpdates.get() > 0;
    }

    public boolean isLoadingComplete() {
        return loadingCompleted.get();
    }

    public void setLoadingComplete(boolean complete) {
        loadingCompleted.set(complete);
    }

    public boolean startLoading() {
        return loadingCompleted.compareAndSet(true, false);
    }
}
