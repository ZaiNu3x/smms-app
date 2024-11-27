package group.intelliboys.smms.orm.repository;

import group.intelliboys.smms.orm.dao.AccidentHistoryDao;
import group.intelliboys.smms.orm.data.AccidentHistory;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.ContextHolder;

public class AccidentHistoryRepository {
    private final AccidentHistoryDao accidentHistoryDao;
    private final DatabaseService databaseService;

    public AccidentHistoryRepository() {
        databaseService = DatabaseService.getInstance(ContextHolder.getInstance().getContext());
        accidentHistoryDao = databaseService.getAppDatabase().accidentHistoryDao();
    }

    public void insertAccidentHistory(AccidentHistory accidentHistory) {
        accidentHistoryDao.insertAccidentHistory(accidentHistory);
    }
}
