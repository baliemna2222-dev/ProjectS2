package JStream.utils;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtils {
	 // Hash password
    public static String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    // Check raw password against hashed
    public static boolean checkPassword(String raw, String hashed) {
        if (raw == null || hashed == null) return false;
        return BCrypt.checkpw(raw, hashed);
    }

}

