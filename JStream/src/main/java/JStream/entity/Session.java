package JStream.entity;

import JStream.entity.UserRole;

public class Session {
    private static int currentUserId = 1;
    private static String currentUsername = "test";
    private static UserRole currentUserRole = UserRole.USER;

    private static String profileImagePath = "/assets/images/profile.png";

    public static void login(int userId, String username, UserRole role) {
        currentUserId = userId;
        currentUsername = username;
        currentUserRole = role != null ? role : UserRole.USER;
    }

    public static void login(int userId, String username) {
        login(userId, username, UserRole.USER);
    }

    public static int getUserId() { return currentUserId; }
    public static String getUsername() { return currentUsername; }

    public static String getProfileImagePath() { return profileImagePath; }
    public static void setProfileImagePath(String path) {
        profileImagePath = path;
    }

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