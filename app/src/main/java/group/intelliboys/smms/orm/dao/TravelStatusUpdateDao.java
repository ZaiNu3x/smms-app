package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import group.intelliboys.smms.orm.data.TravelStatusUpdate;

@Dao
public interface TravelStatusUpdateDao {
    @Insert
    void insertTravelStatusUpdate(TravelStatusUpdate statusUpdate);

    @Query("SELECT * FROM TravelStatusUpdate WHERE travelStatusUpdateId = :id AND address IS NULL")
    TravelStatusUpdate getTravelStatusUpdatesWithNullAddress(String id);

    @Transaction
    @Update
    void updateTravelStatusUpdate(TravelStatusUpdate statusUpdate);
}
