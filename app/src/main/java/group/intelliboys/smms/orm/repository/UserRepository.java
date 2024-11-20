package group.intelliboys.smms.orm.repository;

import java.util.List;

import group.intelliboys.smms.orm.dao.UserDao;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.services.DatabaseService;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(DatabaseService databaseService) {
        userDao = databaseService.getAppDatabase().userDao();
    }

    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public List<User> getAllUser() {
        return userDao.getAllUsers();
    }

    // THIS BLOCK OF CODE USE FOR CLEARING ALL USER INFORMATION IN DATABASE
    public void deleteAllUsers() {
        userDao.deleteAllUsers();
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public User getAuthenticatedUser() {
        return userDao.getAuthenticatedUser();
    }
}
