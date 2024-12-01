package group.intelliboys.smms.orm.repository;

import java.util.List;

import group.intelliboys.smms.orm.dao.TravelStatusUpdateAccidentHistoryDao;
import group.intelliboys.smms.orm.data.TravelStatusUpdateWithAccidentHistory;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.ContextHolder;

public class TravelStatusUpdateAccidentHistoryRepository {
    private TravelStatusUpdateAccidentHistoryDao statusUpdateAccidentHistoryDao;
    private DatabaseService databaseService;

    public TravelStatusUpdateAccidentHistoryRepository() {
        databaseService = DatabaseService.getInstance(ContextHolder.getInstance().getContext());
        statusUpdateAccidentHistoryDao = databaseService.getAppDatabase().statusUpdateAccidentHistoryDao();
    }

    public List<TravelStatusUpdateWithAccidentHistory> getAccidentHistories() {
        return statusUpdateAccidentHistoryDao.getTravelStatusUpdateWithAccidentHistory();
    }
}
