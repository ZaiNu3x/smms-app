package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import group.intelliboys.smms.orm.data.TravelHistory;

@Dao
public interface TravelHistoryDao {
    @Insert
    void insertTravelHistory(TravelHistory travelHistory);

    @Update
    void updateTravelHistory(TravelHistory travelHistory);

    @Query("SELECT * FROM TravelHistory WHERE startLocationName IS NULL")
    List<TravelHistory> getTravelHistoriesWithNullStartLocation();

    @Query("SELECT * FROM TravelHistory WHERE endLocationName IS NULL")
    List<TravelHistory> getTravelHistoriesWithNullEndLocation();

    @Query("SELECT * FROM TravelHistory ORDER BY createdAt")
    List<TravelHistory> getAllTravelHistories();

    @Query("SELECT * FROM TravelHistory WHERE travelHistoryId = :id")
    TravelHistory getTravelHistoryById(String id);
}
