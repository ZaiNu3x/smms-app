package group.intelliboys.smms.orm;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import group.intelliboys.smms.configs.DatabaseConfig;
import group.intelliboys.smms.orm.dao.AccidentHistoryDao;
import group.intelliboys.smms.orm.dao.SearchedLocationDao;
import group.intelliboys.smms.orm.dao.TravelHistoryDao;
import group.intelliboys.smms.orm.dao.TravelStatusUpdateAccidentHistoryDao;
import group.intelliboys.smms.orm.dao.TravelStatusUpdateDao;
import group.intelliboys.smms.orm.dao.UserDao;
import group.intelliboys.smms.orm.data.AccidentHistory;
import group.intelliboys.smms.orm.data.SearchedLocation;
import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.orm.data.TravelStatusUpdate;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.utils.converters.LocalDateConverter;
import group.intelliboys.smms.utils.converters.LocalDateTimeConverter;

@Database(entities = {User.class, TravelHistory.class, TravelStatusUpdate.class,
        AccidentHistory.class, SearchedLocation.class},
        version = DatabaseConfig.VERSION, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract TravelHistoryDao travelHistoryDao();

    public abstract TravelStatusUpdateDao travelStatusUpdateDao();

    public abstract AccidentHistoryDao accidentHistoryDao();

    public abstract SearchedLocationDao searchedLocationDao();

    public abstract TravelStatusUpdateAccidentHistoryDao statusUpdateAccidentHistoryDao();
}
