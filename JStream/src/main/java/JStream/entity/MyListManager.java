package JStream.entity;

import java.util.ArrayList;
import java.util.List;

public class MyListManager {
    private static MyListManager instance;
    private List<AddToListListener> listeners = new ArrayList<>();

    private MyListManager() {}

    public static MyListManager getInstance() {
        if (instance == null) instance = new MyListManager();
        return instance;
    }

    // Listener interface
    public interface AddToListListener {
        void onItemUpdated(int filmId, int serieId);
    }

    // Add/remove listeners
    public void addListener(AddToListListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AddToListListener listener) {
        listeners.remove(listener);
    }

    // Notify all listeners when an item is added/removed
    public void notifyItemUpdated(int filmId, int serieId) {
        for (AddToListListener l : listeners) {
            l.onItemUpdated(filmId, serieId);
        }
    }
}
