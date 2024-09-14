package group.intelliboys.smms.services;

import android.content.Context;

public class Utils {
    private static Utils utilsInstance;
    private Context applicationContext;

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
}
