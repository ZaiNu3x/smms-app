package group.intelliboys.smms.services.local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import group.intelliboys.smms.configs.local_db.DatabaseHelper;
import group.intelliboys.smms.models.data.SearchedPlace;
import group.intelliboys.smms.services.Utils;

public class LocalDbSearchedPlaceService {
    private DatabaseHelper databaseHelper;
    private Activity activityRef;
    private Context context;

    public LocalDbSearchedPlaceService(Activity activity) {
        this.databaseHelper = DatabaseHelper.getInstance();
        this.activityRef = activity;
        this.context = Utils.getInstance().getApplicationContext();
    }

    public void insertSearchedPlace(SearchedPlace searchedPlace) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", searchedPlace.getName());
        contentValues.put("display_name", searchedPlace.getDisplayName());
        contentValues.put("latitude", searchedPlace.getLatitude());
        contentValues.put("longitude", searchedPlace.getLongitude());
        contentValues.put("bounding_box", searchedPlace.getBoundingBox());

        long result = sqLiteDatabase.insert("searched_place", null, contentValues);

        if (result != -1) {
            activityRef.runOnUiThread(() -> {
                //Toast.makeText(context, "Inserted Successful!", Toast.LENGTH_SHORT).show();
            });
        } else {
            activityRef.runOnUiThread(() -> {
                //Toast.makeText(context, "Insertion Failed!", Toast.LENGTH_LONG).show();
            });
        }
    }

    public void deleteSearchedPlace(int id) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        sqLiteDatabase.delete("User", "id = ?", new String[]{Integer.toString(id)});
    }

    public List<SearchedPlace> getSearchedPlaces(String keywords) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM searched_place WHERE display_name LIKE '%" + keywords + "%'";
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        List<SearchedPlace> searchedPlaces = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                SearchedPlace searchedPlace = SearchedPlace.builder()
                        .name(cursor.getString(cursor.getColumnIndex("name")))
                        .displayName(cursor.getString(cursor.getColumnIndex("display_name")))
                        .latitude(cursor.getDouble(cursor.getColumnIndex("latitude")))
                        .longitude(cursor.getDouble(cursor.getColumnIndex("longitude")))
                        .boundingBox(cursor.getString(cursor.getColumnIndex("bounding_box")))
                        .build();

                searchedPlaces.add(searchedPlace);
            }
            while (cursor.moveToNext());
        }

        return searchedPlaces;
    }
}
