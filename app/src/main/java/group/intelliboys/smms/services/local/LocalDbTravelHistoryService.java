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

import group.intelliboys.smms.configs.local_db.DatabaseHelper;
import group.intelliboys.smms.models.data.TravelHistory;
import group.intelliboys.smms.services.Utils;

public class LocalDbTravelHistoryService {
    private final DatabaseHelper databaseHelper;
    private final Context context;
    private final Activity activityRef;
    private final LocalDbUserService userService;

    public LocalDbTravelHistoryService(Activity activity) {
        this.databaseHelper = DatabaseHelper.getInstance();
        this.context = Utils.getInstance().getApplicationContext();
        this.activityRef = activity;
        this.userService = new LocalDbUserService(activity);
    }

    public void addTravelHistory(TravelHistory travelHistory) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id", travelHistory.getId());
        cv.put("user_id", travelHistory.getUserId());
        cv.put("start_time", travelHistory.getStartTime().toString());
        cv.put("end_time", travelHistory.getEndTime() != null ? travelHistory.getEndTime().toString() : null);
        cv.put("start_coordinates", formatCoordinates(travelHistory.getStartLatitude(), travelHistory.getStartLongitude(), travelHistory.getStartAltitude()));
        cv.put("end_coordinates", formatCoordinates(travelHistory.getEndLatitude(), travelHistory.getEndLongitude(), travelHistory.getEndAltitude()));
        cv.put("created_at", travelHistory.getCreatedAt().toString());

        long result = sqLiteDatabase.insert("travel_history", null, cv);
        handleInsertResult(result);
        sqLiteDatabase.close();
    }

    public List<TravelHistory> getTravelHistoriesByUser(String email) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM travel_history WHERE user_id = ?";
        List<TravelHistory> travelHistories = new ArrayList<>();

        try (Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{email})) {
            while (cursor.moveToNext()) {
                try {
                    @SuppressLint("Range") TravelHistory travel = TravelHistory.builder()
                            .id(cursor.getString(cursor.getColumnIndex("id")))
                            .userId(cursor.getString(cursor.getColumnIndex("user_id")))
                            .startTime(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("start_time"))))
                            .startLatitude(parseCoordinate(cursor.getString(cursor.getColumnIndex("start_coordinates")), 0))
                            .startLongitude(parseCoordinate(cursor.getString(cursor.getColumnIndex("start_coordinates")), 1))
                            .startAltitude(parseCoordinate(cursor.getString(cursor.getColumnIndex("start_coordinates")), 2))
                            .endLatitude(parseCoordinate(cursor.getString(cursor.getColumnIndex("end_coordinates")), 0))
                            .endLongitude(parseCoordinate(cursor.getString(cursor.getColumnIndex("end_coordinates")), 1))
                            .endAltitude(parseCoordinate(cursor.getString(cursor.getColumnIndex("end_coordinates")), 2))
                            .startLocationName(cursor.getString(cursor.getColumnIndex("start_location_name")))
                            .endTime(cursor.isNull(cursor.getColumnIndex("end_time")) ? null : LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("end_time"))))
                            .endLocationName(cursor.getString(cursor.getColumnIndex("end_location_name")))
                            .createdAt(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("created_at"))))
                            .build();

                    travelHistories.add(travel);
                } catch (Exception e) {
                    Log.e("LocalDbTravelHistoryService", "Error parsing TravelHistory", e);
                }
            }
        }
        sqLiteDatabase.close();
        return travelHistories;
    }

    public void updateTravelHistoryById(TravelHistory travelHistory) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("start_time", travelHistory.getStartTime().toString());
        cv.put("end_time", travelHistory.getEndTime() != null ? travelHistory.getEndTime().toString() : null);
        cv.put("start_coordinates", formatCoordinates(travelHistory.getStartLatitude(), travelHistory.getStartLongitude(), travelHistory.getStartAltitude()));
        cv.put("end_coordinates", formatCoordinates(travelHistory.getEndLatitude(), travelHistory.getEndLongitude(), travelHistory.getEndAltitude()));

        int result = sqLiteDatabase.update("travel_history", cv, "id = ?", new String[]{travelHistory.getId()});
        handleUpdateResult(result);
        sqLiteDatabase.close();
    }

    public void deleteTravelHistoryById(String travelHistoryId) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        int result = sqLiteDatabase.delete("travel_history", "id = ?", new String[]{travelHistoryId});
        handleDeleteResult(result);
        sqLiteDatabase.close();
    }

    private void handleInsertResult(long result) {
        activityRef.runOnUiThread(() -> {
            if (result != -1) {
                Toast.makeText(activityRef, "Insertion Success!", Toast.LENGTH_LONG).show();
                userService.incrementUserVersion();
            } else {
                Toast.makeText(activityRef, "Insertion Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleUpdateResult(int result) {
        activityRef.runOnUiThread(() -> {
            if (result > 0) {
                Toast.makeText(activityRef, "Update Success!", Toast.LENGTH_LONG).show();
                userService.incrementUserVersion();
            } else {
                Toast.makeText(activityRef, "Update Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleDeleteResult(int result) {
        activityRef.runOnUiThread(() -> {
            if (result > 0) {
                Toast.makeText(activityRef, "Delete Success!", Toast.LENGTH_LONG).show();
                userService.incrementUserVersion();
            } else {
                Toast.makeText(activityRef, "Delete Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String formatCoordinates(float latitude, float longitude, float altitude) {
        return latitude + "," + longitude + "," + altitude;
    }

    private float parseCoordinate(String coordinates, int index) {
        String[] parts = coordinates.split(",");
        if (parts.length > index) {
            return Float.parseFloat(parts[index]);
        }
        return 0; // Default value if parsing fails
    }
}
