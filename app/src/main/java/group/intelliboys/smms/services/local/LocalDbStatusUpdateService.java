package group.intelliboys.smms.services.local;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
}
