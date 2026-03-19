package JStream.entity;

public class Session {
    private static int currentUserId = 1;
    private static String currentUsername;

    // Call this when login succeeds
    public static void login(int userId, String username) {
        currentUserId = userId;
        currentUsername = username;
    }

    // Getters
    public static int getUserId() { return currentUserId; }
    public static String getUsername() { return currentUsername; }

    // Logout
    public static void logout() {
        currentUserId = 0;
        currentUsername = null;
    }

    public static boolean isLoggedIn() {
        return currentUserId != 0;
    }
}