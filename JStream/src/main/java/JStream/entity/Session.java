package JStream.entity;

public class Session {
    private static int currentUserId = 1;
    private static String currentUsername = "test";

    // New field for profile image path
    private static String profileImagePath = "/assets/images/profile.png"; // default image

    // Call this when login succeeds
    public static void login(int userId, String username) {
        currentUserId = userId;
        currentUsername = username;
    }

    // Getters
    public static int getUserId() { return currentUserId; }
    public static String getUsername() { return currentUsername; }

    // Profile image getter & setter
    public static String getProfileImagePath() { return profileImagePath; }
    public static void setProfileImagePath(String path) {
        profileImagePath = path;
    }

    // Logout
    public static void logout() {
        currentUserId = 0;
        currentUsername = null;
        profileImagePath = "/assets/images/profile.png"; // reset to default
    }

    public static boolean isLoggedIn() {
        return currentUserId != 0;
    }
	public static void setUserId(int currentUserId) {
		Session.currentUserId = currentUserId;
	}
	public static void setUsername(String currentUsername) {
		Session.currentUsername = currentUsername;
	}
}