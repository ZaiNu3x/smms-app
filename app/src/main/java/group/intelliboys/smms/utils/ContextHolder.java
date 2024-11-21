package group.intelliboys.smms.utils;

import android.content.Context;

import lombok.Getter;
import lombok.Setter;

public class ContextHolder {
    private static ContextHolder instance;

    @Getter
    @Setter
    private Context context;

    private ContextHolder() {
    }

    public static ContextHolder getInstance() {
        if (instance == null) {
            instance = new ContextHolder();
        }

        return instance;
    }
}
