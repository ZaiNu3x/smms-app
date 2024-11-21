package group.intelliboys.smms.orm;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import group.intelliboys.smms.configs.DatabaseConfig;
import group.intelliboys.smms.orm.dao.UserDao;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.utils.Converter;

@Database(entities = {User.class}, version = DatabaseConfig.VERSION, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
