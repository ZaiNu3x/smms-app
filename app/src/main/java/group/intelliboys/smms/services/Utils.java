package group.intelliboys.smms.services;

import android.content.Context;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.models.view_models.UserViewModel;
import lombok.Setter;

public class Utils {
    private static Utils utilsInstance;
    @Setter
    private Context applicationContext;
    private UserViewModel loggedInUser;

    private Utils() {
        loggedInUser = new UserViewModel();
    }

    public static Utils getInstance() {
        if (utilsInstance == null) {
            utilsInstance = new Utils();
            return utilsInstance;
        } else return utilsInstance;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public UserViewModel getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        loggedInUser.updateUserModel(user);
    }
}
