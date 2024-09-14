package group.intelliboys.smms.services.local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;

import group.intelliboys.smms.configs.local_db.DatabaseHelper;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.services.Utils;

public class LocalDbUserService {
    private final DatabaseHelper databaseHelper;
    private final Context context;
    private Activity activityRef;

    public LocalDbUserService(Activity activity) {
        this.databaseHelper = DatabaseHelper.getInstance();
        this.activityRef = activity;
        this.context = Utils.getInstance().getApplicationContext();
    }

    @SuppressLint({"Recycle", "Range"})
    public User retrieveUser(String email) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM user WHERE email = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                return User.builder()
                        .email(cursor.getString(cursor.getColumnIndex("email")))
                        .phoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")))
                        .lastName(cursor.getString(cursor.getColumnIndex("last_name")))
                        .firstName(cursor.getString(cursor.getColumnIndex("first_name")))
                        .middleName(cursor.getString(cursor.getColumnIndex("middle_name")))
                        .sex(cursor.getString(cursor.getColumnIndex("sex")))
                        .birthDate(LocalDate.parse(cursor.getString(cursor.getColumnIndex("last_name"))))
                        .age((byte) cursor.getInt(cursor.getColumnIndex("age")))
                        .address(cursor.getString(cursor.getColumnIndex("address")))
                        .profilePic(cursor.getBlob(cursor.getColumnIndex("profile_pic")))
                        .authToken(cursor.getString(cursor.getColumnIndex("auth_token")))
                        .build();
            }
            while (cursor.moveToNext());
        } else return null;
    }

    @SuppressLint("Range")
    public User retrieveLoggedInUser() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM user LIMIT 1";
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
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
            while (cursor.moveToNext());
        } else return null;
    }

    public void addUser(User user) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

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

        long result = sqLiteDatabase.insert("User", null, contentValues);

        if (result != -1) {
            activityRef.runOnUiThread(() -> {
                //Toast.makeText(context, "User Insertion Success!", Toast.LENGTH_LONG).show();
            });
        } else {
            activityRef.runOnUiThread(() -> {
                //Toast.makeText(context, "User Insertion Failed!", Toast.LENGTH_LONG).show();
            });
        }
    }

    @SuppressLint("Recycle")
    public void deleteUser(String email) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        sqLiteDatabase.delete("User", "email = ?", new String[]{email});
    }
}
