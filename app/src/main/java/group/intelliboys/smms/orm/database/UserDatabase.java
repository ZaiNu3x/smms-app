package group.intelliboys.smms.orm.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import group.intelliboys.smms.orm.dao.UserDao;
import group.intelliboys.smms.orm.data.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
