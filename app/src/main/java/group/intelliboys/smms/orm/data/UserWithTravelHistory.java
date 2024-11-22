package group.intelliboys.smms.orm.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UserWithTravelHistory {
    @Embedded
    public User user;

    @Relation(
            parentColumn = "email",
            entityColumn = "userId")
    public List<TravelHistory> travelHistories;
}
