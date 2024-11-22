package group.intelliboys.smms.orm.repository;

import java.util.List;

import group.intelliboys.smms.orm.dao.UserDao;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.ContextHolder;

public class UserRepository {
    private final UserDao userDao;
    private final DatabaseService databaseService;

    public UserRepository() {
        databaseService = DatabaseService.getInstance(ContextHolder.getInstance()
                .getContext());

        userDao = databaseService.getAppDatabase().userDao();
    }

    public void insertUser(User user) {
        databaseService.getAppDatabase().getTransactionExecutor().execute(() -> {
            userDao.insertUser(user);
        });
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public List<User> getAllUser() {
        return userDao.getAllUsers();
    }

    // THIS BLOCK OF CODE USE FOR CLEARING ALL USER INFORMATION IN DATABASE
    public void deleteAllUsers() {
        databaseService.getAppDatabase().getTransactionExecutor().execute(userDao::deleteAllUsers);
    }

    public void updateUser(User user) {
        databaseService.getAppDatabase().getTransactionExecutor().execute(() -> {
            userDao.updateUser(user);
        });
    }

    public User getAuthenticatedUser() {
        return userDao.getAuthenticatedUser();
    }
}
