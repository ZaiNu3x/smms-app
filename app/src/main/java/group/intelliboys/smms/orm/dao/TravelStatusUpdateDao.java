package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import group.intelliboys.smms.orm.data.TravelStatusUpdate;

@Dao
public interface TravelStatusUpdateDao {
    @Insert
    void insertTravelStatusUpdate(TravelStatusUpdate statusUpdate);
}
