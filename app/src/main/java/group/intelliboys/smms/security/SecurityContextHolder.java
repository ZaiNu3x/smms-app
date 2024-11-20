package group.intelliboys.smms.security;

import group.intelliboys.smms.orm.data.User;
import lombok.Getter;
import lombok.Setter;

public class SecurityContextHolder {
    private static SecurityContextHolder instance;

    @Getter
    @Setter
    private User authenticatedUser;

    private SecurityContextHolder() {
    }

    public static SecurityContextHolder getInstance() {
        if (instance == null) {
            instance = new SecurityContextHolder();
        }

        return instance;
    }
}
