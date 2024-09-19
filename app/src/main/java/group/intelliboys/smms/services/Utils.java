package group.intelliboys.smms.services;

import android.content.Context;
import group.intelliboys.smms.models.data.User;

public class Utils {
    private static Utils utilsInstance;
    private Context applicationContext;
    private User loggedInUser;

    private Utils() {
    }

    public static Utils getInstance() {
        if (utilsInstance == null) {
            utilsInstance = new Utils();
            return utilsInstance;
        } else return utilsInstance;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
