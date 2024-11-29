package group.intelliboys.smms.orm.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import group.intelliboys.smms.orm.data.SearchedLocation;

@Dao
public interface SearchedLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSearchedLocation(SearchedLocation searchedLocation);

    @Query("SELECT * FROM SearchedLocation WHERE displayName LIKE :keywords")
    List<SearchedLocation> getSearchedLocationsByKeyword(String keywords);
}
