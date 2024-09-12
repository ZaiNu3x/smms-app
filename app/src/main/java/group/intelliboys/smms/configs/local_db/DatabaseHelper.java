package group.intelliboys.smms.configs.local_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import group.intelliboys.smms.models.data.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "motopotato.db";
    private static final int DB_VERSION = 1;
    private Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createUserTableQuery =
                "CREATE TABLE user (" +
                        "email VARCHAR(32) PRIMARY KEY, " +
                        "auth_token VARCHAR(512) NOT NULL, " +
                        "profile_pic MEDIUMBLOB);";

        sqLiteDatabase.execSQL(createUserTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropUserTableQuery = "DROP TABLE IF EXISTS user";
        sqLiteDatabase.execSQL(dropUserTableQuery);
        onCreate(sqLiteDatabase);
    }

    public void addUser(User user) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("email", user.getEmail());
        contentValues.put("auth_token", user.getAuthToken());
        contentValues.put("profile_pic", user.getProfilePic());

        long result = sqLiteDatabase.insert("User", null, contentValues);

        if (result == -1) {
            Toast.makeText(context, "Insertion Failed!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(context, "Insertion Success!", Toast.LENGTH_LONG).show();
    }

    public void deleteUser(String email) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.remove("email");
    }
}
