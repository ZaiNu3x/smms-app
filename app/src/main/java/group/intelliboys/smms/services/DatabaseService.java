package group.intelliboys.smms.services;

import android.content.Context;

import androidx.room.Room;

import group.intelliboys.smms.orm.AppDatabase;

public class DatabaseService {
    private static DatabaseService instance;
    private AppDatabase appDatabase;

    public DatabaseService(Context context) {
        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "app_database").build();
    }

    public static DatabaseService getAppDatabase(Context context) {
        if (instance == null) {
            instance = new DatabaseService(context);
        }

        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
