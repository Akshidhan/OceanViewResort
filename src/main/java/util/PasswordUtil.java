package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static boolean matches(String raw, String hash) {
        return raw != null && hash != null && BCrypt.checkpw(raw, hash);
    }
}
