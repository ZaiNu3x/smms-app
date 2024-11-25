package group.intelliboys.smms.orm.repository;

import group.intelliboys.smms.orm.dao.TravelStatusUpdateDao;
import group.intelliboys.smms.orm.data.TravelStatusUpdate;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.ContextHolder;

public class TravelStatusUpdateRepository {
    private final TravelStatusUpdateDao travelStatusUpdateDao;
    private final DatabaseService databaseService;

    public TravelStatusUpdateRepository() {
        databaseService = DatabaseService.getInstance(ContextHolder.getInstance().getContext());
        travelStatusUpdateDao = databaseService.getAppDatabase().travelStatusUpdateDao();
    }

    public void insertTravelStatusUpdate(TravelStatusUpdate statusUpdate) {
        databaseService.getAppDatabase().getTransactionExecutor().execute(() -> {
            travelStatusUpdateDao.insertTravelStatusUpdate(statusUpdate);
        });
    }
}
