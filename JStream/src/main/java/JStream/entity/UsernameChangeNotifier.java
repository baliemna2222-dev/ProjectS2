package JStream.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//Singleton notifier class
public class UsernameChangeNotifier {
 private static final List<Consumer<String>> listeners = new ArrayList<>();

 public static void addListener(Consumer<String> listener) {
     listeners.add(listener);
 }

 public static void removeListener(Consumer<String> listener) {
     listeners.remove(listener);
 }

 public static void notifyAllListeners(String newUsername) {
     for (Consumer<String> listener : listeners) {
         listener.accept(newUsername);
     }
 }
}
