package group.intelliboys.smms.orm.repository;

import group.intelliboys.smms.orm.dao.TravelHistoryDao;
import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.ContextHolder;

public class TravelHistoryRepository {
    private TravelHistoryDao travelHistoryDao;

    public TravelHistoryRepository() {
        DatabaseService service = DatabaseService.getInstance(ContextHolder.getInstance()
                .getContext());

        travelHistoryDao = service.getAppDatabase().travelHistoryDao();
    }

    public void insertTravelHistory(TravelHistory travelHistory) {
        travelHistoryDao.insertTravelHistory(travelHistory);
    }

    public void getTravelHistoryByUserEmail(String userId) {
        travelHistoryDao.getTravelHistoryByUserId(userId);
    }
}
