package group.intelliboys.smms.configs;

import android.os.Build;
import android.provider.Settings;

public class DeviceSpecs {
    public static final String DEVICE_NAME = Build.MANUFACTURER + " " + Build.MODEL;
    public static final String DEVICE_ID = Settings.Secure.ANDROID_ID;
}
