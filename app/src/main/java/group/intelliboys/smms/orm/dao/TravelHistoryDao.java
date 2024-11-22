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

    @Query("SELECT * FROM travel_history WHERE userId = :id")
    TravelHistory getTravelHistoryByUserId(String id);

    @Query("SELECT * FROM travel_history WHERE userId = :userId")
    List<TravelHistory> getAllTravelHistoriesByUserId(String userId);

    @Update
    void updateTravelHistory(TravelHistory travelHistory);

    @Query("DELETE FROM travel_history WHERE travelHistoryId = :id")
    void deleteTravelHistoryById(long id);
}
