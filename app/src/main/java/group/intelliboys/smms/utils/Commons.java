package group.intelliboys.smms.utils;

import android.content.Context;
import android.widget.Toast;

public class Commons {
    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
