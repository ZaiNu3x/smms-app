package group.intelliboys.smms.utils;

import android.content.Context;
import android.widget.Toast;

public class Commons {
    private static Toast toast;

    public static void toastMessage(Context context, String message) {
        if (toast == null) {
            toast = new Toast(context);
        }

        toast.setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
