package group.intelliboys.smms.configs.local_db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import group.intelliboys.smms.services.Utils;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper databaseHelperInstance;

    private static final String DATABASE_NAME = "motopotato.db";
    private static final int DATABASE_VERSION = 1;

    private static final String USER_TABLE = "CREATE TABLE user (" +
            "version BIGINT NOT NULL, " +
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

    private static final String SEARCHED_PLACE_TABLE = "CREATE TABLE searched_place (" +
            "name VARCHAR(255) NOT NULL, " +
            "display_name VARCHAR(255) PRIMARY KEY, " +
            "latitude DOUBLE NOT NULL, " +
            "longitude DOUBLE NOT NULL, " +
            "bounding_box VARCHAR(255) NOT NULL, " +
            "user_id VARCHAR(64) NOT NULL, " +
            "FOREIGN KEY (user_id) REFERENCES user (email) ON DELETE CASCADE" +
            ")";

    private static final String TRAVEL_HISTORY_TABLE = "CREATE TABLE travel_history (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "user_id VARCHAR(64) NOT NULL, " +
            "start_time TIMESTAMP NOT NULL, " +
            "end_time TIMESTAMP, " +
            "start_coordinates VARCHAR(255) NOT NULL, " +
            "start_location_name VARCHAR(255), " +
            "end_location_name VARCHAR(255), " +
            "end_coordinates VARCHAR(255), " +
            "created_at TIMESTAMP NOT NULL, " +
            "FOREIGN KEY (user_id) REFERENCES user(email) ON DELETE CASCADE" +
            ")";

    private static final String STATUS_UPDATE_TABLE = "CREATE TABLE status_update (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "latitude VARCHAR(32) NOT NULL, " +
            "longitude VARCHAR(32) NOT NULL, " +
            "altitude VARCHAR(32) NOT NULL, " +
            "cornering_angle DECIMAL(3, 2) NOT NULL, " +
            "speed INTEGER NOT NULL, " +
            "direction VARCHAR(32) NOT NULL, " +
            "created_at TIMESTAMP NOT NULL, " +
            "is_wearing_helmet BIT(1) NOT NULL, " +
            "fk_travel_history VARCHAR(36) NOT NULL, " +
            "FOREIGN KEY (fk_travel_history) REFERENCES travel_history(id) ON DELETE CASCADE" +
            ")";

    private static final String ACCIDENT_HISTORY_TABLE = "CREATE TABLE accident_history (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "front_camera_snap MEDIUMBLOB, " +
            "back_camera_snap MEDIUMBLOB, " +
            "message VARCHAR(255) NOT NULL, " +
            "fk_status_update VARCHAR(36) NOT NULL, " +
            "FOREIGN KEY (fk_status_update) REFERENCES status_update(id) ON DELETE CASCADE" +
            ")";

    private DatabaseHelper() {
        super(Utils.getInstance().getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance() {
        if (databaseHelperInstance == null) {
            databaseHelperInstance = new DatabaseHelper();
        }
        return databaseHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(USER_TABLE);
        sqLiteDatabase.execSQL(SEARCHED_PLACE_TABLE);
        sqLiteDatabase.execSQL(TRAVEL_HISTORY_TABLE);
        sqLiteDatabase.execSQL(STATUS_UPDATE_TABLE);
        sqLiteDatabase.execSQL(ACCIDENT_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop all tables if they exist
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS accident_history");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS status_update");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS travel_history");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS searched_place");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");

        onCreate(sqLiteDatabase);
    }
}
