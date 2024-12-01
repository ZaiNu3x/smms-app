package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import group.intelliboys.smms.orm.data.TravelStatusUpdateWithAccidentHistory;

@Dao
public interface TravelStatusUpdateAccidentHistoryDao {

    @Query("SELECT * FROM AccidentHistory a JOIN TravelStatusUpdate t " +
            "ON a.travelStatusUpdateId = t.travelStatusUpdateId")
    List<TravelStatusUpdateWithAccidentHistory> getTravelStatusUpdateWithAccidentHistory();
}
