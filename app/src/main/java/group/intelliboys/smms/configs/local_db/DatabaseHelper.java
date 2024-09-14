package group.intelliboys.smms.configs.local_db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import group.intelliboys.smms.services.Utils;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper databaseHelperInstance;

    private DatabaseHelper() {
        super(Utils.getInstance().getApplicationContext(),
                "motopotato.db", null, 1);
    }

    public static DatabaseHelper getInstance() {
        if (databaseHelperInstance == null) {
            databaseHelperInstance = new DatabaseHelper();
        }
        return databaseHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String createTables = "CREATE TABLE user (" +
                "email VARCHAR(64) PRIMARY KEY, " +
                "phone_number VARCHAR(13) NOT NULL, " +
                "last_name VARCHAR(32) NOT NULL, " +
                "first_name VARCHAR(32) NOT NULL, " +
                "middle_name VARCHAR(32), " +
                "sex VARCHAR(1) NOT NULL, " +
                "birth_date DATE NOT NULL, " +
                "age TINYINT NOT NULL, " +
                "address VARCHAR(255) NOT NULL, " +
                "profile_pic MEDIUMBLOB, " +
                "auth_token VARCHAR(512) NOT NULL" +
                ")";

        sqLiteDatabase.execSQL(createTables);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String dropTables = "DROP TABLE IF EXISTS user";
        sqLiteDatabase.execSQL(dropTables);
        onCreate(sqLiteDatabase);
    }
}
