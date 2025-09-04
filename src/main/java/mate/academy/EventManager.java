package mate.academy;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManager {
    private final Set<EventListener> listenerSet = new CopyOnWriteArraySet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void registerListener(EventListener listener) {
        listenerSet.add(listener);
    }

    public void deregisterListener(EventListener listener) {
        listenerSet.remove(listener);
    }

    public void notifyEvent(Event event) {
        for (EventListener listener : listenerSet) {
            executorService.submit(() -> listener.onEvent(event));
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
