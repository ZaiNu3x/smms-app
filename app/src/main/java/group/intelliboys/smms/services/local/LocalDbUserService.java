package group.intelliboys.smms.services.local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Objects;

import group.intelliboys.smms.configs.local_db.DatabaseHelper;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.services.Utils;

public class LocalDbUserService {
    private final DatabaseHelper databaseHelper;
    private final Context context;
    private final Activity activityRef;

    public LocalDbUserService(Activity activity) {
        this.databaseHelper = DatabaseHelper.getInstance();
        this.activityRef = activity;
        this.context = Utils.getInstance().getApplicationContext();
    }

    @SuppressLint({"Recycle", "Range"})
    public User retrieveUser(String email) {
        return queryUser("SELECT * FROM user WHERE email = ?", new String[]{email});
    }

    @SuppressLint("Range")
    public User retrieveLoggedInUser() {
        return queryUser("SELECT * FROM user LIMIT 1", null);
    }

    @SuppressLint("Range")
    private User queryUser(String query, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs)) {
            if (cursor.moveToFirst()) {
                return mapCursorToUser(cursor);
            }
        }
        return null;
    }

    public void updateLoggedInUserInfo(User user) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = mapUserToContentValues(user);

        int result = sqLiteDatabase.update("user", contentValues, "email = ?", new String[]{user.getEmail()});
        showToast(result > 0 ? "User Update Success!" : "User Update Fail!");

        if (result > 0) {
            incrementUserVersion();
        }
    }

    public void addUser(User user) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = mapUserToContentValues(user);

        long result = sqLiteDatabase.insert("user", null, contentValues);
        showToast(result != -1 ? "User Insertion Success!" : "User Insertion Failed!");
    }

    public void incrementUserVersion() {
        String email = Objects.requireNonNull(Utils.getInstance().getLoggedInUser().getUserModel().getValue()).getEmail();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE user SET version = version + 1 WHERE email = ?", new Object[]{email});
    }

    @SuppressLint("Range")
    public long getUserAccountVersion(String email) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery("SELECT version FROM user WHERE email = ?", new String[]{email})) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex("version"));
            }
        }
        return 0;
    }

    @SuppressLint("Recycle")
    public void deleteUser(String email) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        int rowsDeleted = sqLiteDatabase.delete("user", "email = ?", new String[]{email});
        if (rowsDeleted > 0) {
            showToast("User Deleted Successfully!");
        } else {
            showToast("User not found or delete failed.");
        }
    }

    @SuppressLint("Range")
    private User mapCursorToUser(Cursor cursor) {
        return User.builder()
                .email(cursor.getString(cursor.getColumnIndex("email")))
                .phoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")))
                .lastName(cursor.getString(cursor.getColumnIndex("last_name")))
                .firstName(cursor.getString(cursor.getColumnIndex("first_name")))
                .middleName(cursor.getString(cursor.getColumnIndex("middle_name")))
                .sex(cursor.getString(cursor.getColumnIndex("sex")))
                .birthDate(LocalDate.parse(cursor.getString(cursor.getColumnIndex("birth_date"))))
                .age((byte) cursor.getInt(cursor.getColumnIndex("age")))
                .address(cursor.getString(cursor.getColumnIndex("address")))
                .profilePic(cursor.getBlob(cursor.getColumnIndex("profile_pic")))
                .authToken(cursor.getString(cursor.getColumnIndex("auth_token")))
                .build();
    }

    private ContentValues mapUserToContentValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("version", user.getVersion());
        contentValues.put("email", user.getEmail());
        contentValues.put("phone_number", user.getPhoneNumber());
        contentValues.put("last_name", user.getLastName());
        contentValues.put("first_name", user.getFirstName());
        contentValues.put("middle_name", user.getMiddleName());
        contentValues.put("sex", user.getSex());
        contentValues.put("birth_date", String.valueOf(user.getBirthDate()));
        contentValues.put("age", user.getAge());
        contentValues.put("address", user.getAddress());
        contentValues.put("profile_pic", user.getProfilePic());
        contentValues.put("auth_token", user.getAuthToken());
        return contentValues;
    }

    private void showToast(String message) {
        activityRef.runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }
}
