package mate.academy;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventManager {
    private final Set<EventListener> listenerSet = new CopyOnWriteArraySet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    public void registerListener(EventListener listener) {
        Objects.requireNonNull(listener, "NPE error");
        listenerSet.add(listener);
    }

    public void deregisterListener(EventListener listener) {
        Objects.requireNonNull(listener, "NPE error");
        listenerSet.remove(listener);
    }

    public void notifyEvent(Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        if (isShutdown.get()) {
            throw new IllegalStateException("EventManager is shut down");
        }
        for (EventListener listener : listenerSet) {
            try {
                executorService.submit(() -> {
                    try {
                        listener.onEvent(event);
                    } catch (Exception e) {
                        System.err.println("Listener " + listener + " failed: " + e.getMessage());
                    }
                });
            } catch (RejectedExecutionException ex) {
                throw new IllegalStateException("EventManager is shut down", ex);
            }
        }
    }

    public void shutdown() {
        isShutdown.set(true);
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
