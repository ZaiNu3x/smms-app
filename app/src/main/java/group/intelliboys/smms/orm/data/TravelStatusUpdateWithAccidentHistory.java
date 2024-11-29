package group.intelliboys.smms.orm.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelStatusUpdateWithAccidentHistory {
    @Embedded
    private TravelStatusUpdate statusUpdate;

    @Relation(
            parentColumn = "travelStatusUpdateId",
            entityColumn = "travelStatusUpdateId"
    )
    private AccidentHistory accidentHistory;
}
