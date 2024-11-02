package group.intelliboys.smms.services.local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import group.intelliboys.smms.configs.local_db.DatabaseHelper;
import group.intelliboys.smms.models.data.TravelHistory;
import group.intelliboys.smms.services.Utils;

public class LocalDbTravelHistoryService {
    private final DatabaseHelper databaseHelper;
    private final Context context;
    private Activity activityRef;

    public LocalDbTravelHistoryService(Activity activity) {
        this.databaseHelper = DatabaseHelper.getInstance();
        this.context = Utils.getInstance().getApplicationContext();
        this.activityRef = activity;
    }

    /*
        THIS BLOCK OF CODE WILL SET THE LOCATION NAME
        IN THE TRAVEL HISTORY TABLE.
     */
    public void setLocationNames() {

    }

    public void addTravelHistory(TravelHistory travelHistory) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id", travelHistory.getId());
        cv.put("user_id", travelHistory.getUserId());
        cv.put("start_time", String.valueOf(travelHistory.getStartTime()));
        cv.put("end_time", String.valueOf(travelHistory.getEndTime()));
        cv.put("start_coordinates", String.valueOf(travelHistory.getStartCoordinates()));
        cv.put("end_coordinates", String.valueOf(travelHistory.getEndCoordinates()));
        cv.put("created_at", String.valueOf(travelHistory.getCreatedAt()));

        long result = sqLiteDatabase.insert("travel_history", null, cv);

        if (result != -1) {
            // CODE FOR SUCCESSFUL INSERTION
            activityRef.runOnUiThread(() -> {
                Toast.makeText(activityRef, "INSERTION SUCCESS!", Toast.LENGTH_LONG).show();
            });
        } else {
            // CODE FOR FAILED INSERTION
            activityRef.runOnUiThread(() -> {
                Toast.makeText(activityRef, "INSERTION FAILED!", Toast.LENGTH_LONG).show();
            });
        }
    }

    public List<TravelHistory> getTravelHistoriesByUser(String email) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM travel_history WHERE user_id = ?";

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{email});
        ArrayList<TravelHistory> travelHistories = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                try {
                    @SuppressLint("Range") TravelHistory travel = TravelHistory.builder()
                            .id(cursor.getString(cursor.getColumnIndex("id")))
                            .userId(cursor.getString(cursor.getColumnIndex("user_id")))
                            .startTime(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("start_time"))))
                            .startCoordinates(cursor.getString(cursor.getColumnIndex("start_coordinates")))
                            .startLocationName(cursor.getString(cursor.getColumnIndex("start_location_name")))
                            .endTime(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("end_time"))))
                            .endCoordinates(cursor.getString(cursor.getColumnIndex("end_coordinates")))
                            .endLocationName(cursor.getString(cursor.getColumnIndex("end_location_name")))
                            .createdAt(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("created_at"))))
                            .build();

                    travelHistories.add(travel);
                } catch (Exception e) {
                    Log.i("", Objects.requireNonNull(e.getMessage()));
                }
            }
            while (cursor.moveToNext());
        }
        return travelHistories;
    }

    public void updateTravelHistoryById(TravelHistory travelHistory) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("start_time", String.valueOf(travelHistory.getStartTime()));
        cv.put("end_time", String.valueOf(travelHistory.getEndTime()));
        cv.put("start_coordinates", String.valueOf(travelHistory.getStartCoordinates()));
        cv.put("end_coordinates", String.valueOf(travelHistory.getEndCoordinates()));

        int result = sqLiteDatabase.update("travel_history", cv, "id = ?",
                new String[]{travelHistory.getId()});

        if (result > 0) {
            activityRef.runOnUiThread(() -> {
                Toast.makeText(context, "Update Success!", Toast.LENGTH_LONG).show();
            });
        } else {
            activityRef.runOnUiThread(() -> {
                Toast.makeText(context, "Update Fail!", Toast.LENGTH_LONG).show();
            });
        }
    }
}
