package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import group.intelliboys.smms.orm.data.AccidentHistory;

@Dao
public interface AccidentHistoryDao {
    @Insert
    void insertAccidentHistory(AccidentHistory accidentHistory);
}
