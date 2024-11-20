package group.intelliboys.smms.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor {
    private static ExecutorService executorService;

    public static void run(Runnable runnable) {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.submit(runnable);
    }
}
