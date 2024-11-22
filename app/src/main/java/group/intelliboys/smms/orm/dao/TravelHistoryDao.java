package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;
import androidx.room.Update;

import group.intelliboys.smms.orm.data.TravelHistory;

@Dao
public interface TravelHistoryDao {
    @Insert
    void insertTravelHistory(TravelHistory travelHistory);

    @Transaction
    @Update
    void updateTravelHistory(TravelHistory travelHistory);
}
