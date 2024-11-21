package group.intelliboys.smms.services;

import android.content.Context;

import androidx.room.Room;

import group.intelliboys.smms.configs.DatabaseConfig;
import group.intelliboys.smms.orm.AppDatabase;

public class DatabaseService {
    private static DatabaseService instance;
    private final AppDatabase appDatabase;

    private DatabaseService(Context context) {
        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, DatabaseConfig.NAME).build();
    }

    public static DatabaseService getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseService(context);
        }

        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
