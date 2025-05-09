package group.intelliboys.smms.orm.repository;

import java.util.List;

import group.intelliboys.smms.orm.dao.TravelHistoryDao;
import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.ContextHolder;

public class TravelHistoryRepository {
    private final TravelHistoryDao travelHistoryDao;
    private final DatabaseService databaseService;

    public TravelHistoryRepository() {
        databaseService = DatabaseService.getInstance(ContextHolder.getInstance()
                .getContext());

        travelHistoryDao = databaseService.getAppDatabase().travelHistoryDao();
    }

    public void insertTravelHistory(TravelHistory travelHistory) {
        travelHistoryDao.insertTravelHistory(travelHistory);
    }

    public void updateTravelHistory(TravelHistory travelHistory) {
        travelHistoryDao.updateTravelHistory(travelHistory);
    }

    public List<TravelHistory> getTravelHistoriesWithNullStartLocation() {
        return travelHistoryDao.getTravelHistoriesWithNullStartLocation();
    }

    public List<TravelHistory> getTravelHistoriesWithNullEndLocation() {
        return travelHistoryDao.getTravelHistoriesWithNullEndLocation();
    }

    public List<TravelHistory> getAllTravelHistories() {
        return travelHistoryDao.getAllTravelHistories();
    }

    public TravelHistory getTravelHistoryById(String id) {
        return travelHistoryDao.getTravelHistoryById(id);
    }
}
