package group.intelliboys.smms.orm;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.time.LocalDate;

import group.intelliboys.smms.configs.DatabaseConfig;
import group.intelliboys.smms.orm.dao.TravelHistoryDao;
import group.intelliboys.smms.orm.dao.UserDao;
import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.utils.converters.LocalDateConverter;
import group.intelliboys.smms.utils.converters.LocalDateTimeConverter;

@Database(entities = {User.class, TravelHistory.class},
        version = DatabaseConfig.VERSION, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract TravelHistoryDao travelHistoryDao();
}
