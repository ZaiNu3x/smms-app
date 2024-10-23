package group.intelliboys.smms.services.local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public void addTravelHistory(TravelHistory travelHistory) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id", travelHistory.getId());
        cv.put("user_id", travelHistory.getUserId());
        cv.put("start_time", String.valueOf(travelHistory.getStartTime()));
        cv.put("end_time", String.valueOf(travelHistory.getEndTime()));
        cv.put("start_location", String.valueOf(travelHistory.getStartLocation()));
        cv.put("end_location", String.valueOf(travelHistory.getEndLocation()));
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
                @SuppressLint("Range")
                String startLocationInStr = cursor.getString(cursor.getColumnIndex("start_location"));
                @SuppressLint("Range")
                String endLocationInStr = cursor.getString(cursor.getColumnIndex("end_location"));


                GeoPoint startLocation = new GeoPoint(0f, 0f, 0f);
                GeoPoint endLocation = new GeoPoint(0f, 0f, 0f);

                if (startLocationInStr != null) {
                    String[] strArr = startLocationInStr.split(",");
                    startLocation.setLatitude(Double.parseDouble(strArr[0]));
                    startLocation.setLongitude(Double.parseDouble(strArr[1]));
                    startLocation.setAltitude(Double.parseDouble(strArr[2]));
                }

                if (endLocationInStr != null) {
                    String[] strArr = endLocationInStr.split(",");
                    endLocation.setLatitude(Double.parseDouble(strArr[0]));
                    endLocation.setLongitude(Double.parseDouble(strArr[1]));
                    endLocation.setAltitude(Double.parseDouble(strArr[2]));
                }

                @SuppressLint("Range")
                TravelHistory travelHistory = TravelHistory.builder()
                        .id(cursor.getString(cursor.getColumnIndex("id")))
                        .userId(cursor.getString(cursor.getColumnIndex("user_id")))
                        .startTime(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("start_time"))))
                        .endTime(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("end_time"))))
                        .startLocation(startLocation)
                        .endLocation(endLocation)
                        .build();

                travelHistories.add(travelHistory);
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
        cv.put("start_location", String.valueOf(travelHistory.getStartLocation()));
        cv.put("end_location", String.valueOf(travelHistory.getEndLocation()));

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
