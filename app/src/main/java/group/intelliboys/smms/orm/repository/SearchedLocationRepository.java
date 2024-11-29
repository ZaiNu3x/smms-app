package group.intelliboys.smms.orm.repository;

import java.util.List;

import group.intelliboys.smms.orm.dao.SearchedLocationDao;
import group.intelliboys.smms.orm.data.SearchedLocation;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.ContextHolder;

public class SearchedLocationRepository {
    private final SearchedLocationDao searchedLocationDao;
    private final DatabaseService databaseService;

    public SearchedLocationRepository() {
        databaseService = DatabaseService.getInstance(ContextHolder.getInstance().getContext());
        searchedLocationDao = databaseService.getAppDatabase().searchedLocationDao();
    }

    public void insertSearchedLocation(SearchedLocation searchedLocation) {
        searchedLocationDao.insertSearchedLocation(searchedLocation);
    }

    public List<SearchedLocation> getSearchedLocationsByKeywords(String keywords) {
        return searchedLocationDao.getSearchedLocationsByKeyword(keywords);
    }
}
