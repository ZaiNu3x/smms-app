package group.intelliboys.smms.services.local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import group.intelliboys.smms.configs.local_db.DatabaseHelper;
import group.intelliboys.smms.models.data.StatusUpdate;
import group.intelliboys.smms.services.Utils;

public class LocalDbStatusUpdateService {
    private final DatabaseHelper databaseHelper;
    private final Context context;
    private Activity activityRef;

    public LocalDbStatusUpdateService(Activity activity) {
        this.databaseHelper = DatabaseHelper.getInstance();
        this.context = Utils.getInstance().getApplicationContext();
        this.activityRef = activity;
    }

    public void insertStatusUpdate(StatusUpdate update) {
        SQLiteDatabase sqliteDb = databaseHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id", update.getId());
        cv.put("latitude", update.getLatitude());
        cv.put("longitude", update.getLongitude());
        cv.put("altitude", update.getAltitude());
        cv.put("cornering_angle", update.getCorneringAngle());
        cv.put("speed", update.getSpeed());
        cv.put("direction", update.getDirection());
        cv.put("created_at", String.valueOf(update.getCreatedAt()));
        cv.put("is_wearing_helmet", update.isWearingHelmet());
        cv.put("fk_travel_history", update.getTravelHistoryId());

        long result = sqliteDb.insert("status_update", null, cv);

        if (result != -1) {
            // CODE FOR SUCCESSFUL INSERTION
            activityRef.runOnUiThread(() -> {

            });
        } else {
            // CODE FOR FAILED INSERTION
            activityRef.runOnUiThread(() -> {

            });
        }
    }

    public List<StatusUpdate> getStatusUpdates() {
        String email = Objects.requireNonNull(Utils.getInstance().getLoggedInUser().getUserModel().getValue()).getEmail();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        final String query = "SELECT * FROM travel_history WHERE email = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{email});
        List<StatusUpdate> statusUpdates = new ArrayList<>();

        if (cursor.moveToNext()) {
            do {
                @SuppressLint("Range")
                StatusUpdate statusUpdate = StatusUpdate.builder()
                        .id(cursor.getString(cursor.getColumnIndex("id")))
                        .travelHistoryId(cursor.getString(cursor.getColumnIndex("fk_travel_history")))
                        .speed(cursor.getInt(cursor.getColumnIndex("speed")))
                        .isWearingHelmet(cursor.getInt(cursor.getColumnIndex("is_wearing_helmet")) > 0)
                        .createdAt(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("created_at"))))
                        .direction(cursor.getString(cursor.getColumnIndex("direction")))
                        .corneringAngle(cursor.getFloat(cursor.getColumnIndex("cornering_angle")))
                        .altitude(cursor.getFloat(cursor.getColumnIndex("altitude")))
                        .longitude(cursor.getFloat(cursor.getColumnIndex("longitude")))
                        .latitude(cursor.getFloat(cursor.getColumnIndex("altitude")))
                        .build();

                Log.i("", statusUpdate.toString());
            }
            while (cursor.moveToNext());
        }

        cursor.close();

        return null;
    }
}
