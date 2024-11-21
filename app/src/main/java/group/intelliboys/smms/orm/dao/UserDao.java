package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import group.intelliboys.smms.orm.data.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM user WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM user LIMIT 1")
    User getAuthenticatedUser();

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("DELETE FROM user")
    void deleteAllUsers();
}
