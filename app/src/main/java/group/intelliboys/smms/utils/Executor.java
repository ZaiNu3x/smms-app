package group.intelliboys.smms.utils;

import java.util.concurrent.Executors;

public class Executor {
    public static void run(Runnable runnable) {
        Executors.newSingleThreadExecutor().submit(runnable);
    }
}
