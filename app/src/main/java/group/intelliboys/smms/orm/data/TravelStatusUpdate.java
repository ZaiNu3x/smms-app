package group.intelliboys.smms.orm.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(tableName = "travel_status_update", foreignKeys = @ForeignKey(entity = TravelHistory.class,
        parentColumns = "travelHistoryId",
        childColumns = "travelHistoryId",
        onDelete = ForeignKey.CASCADE))
@JsonIgnoreProperties(ignoreUnknown = true)
public class TravelStatusUpdate implements Serializable {
    @NonNull
    @PrimaryKey
    private String travelStatusUpdateId;
    private String travelHistoryId;
    private float latitude;
    private float longitude;
    private float altitude;
    private int speedInKmh;
    private String direction;
    private float ridingAngle;
    private boolean isWearingHelmet;
    @NonNull
    private LocalDateTime createdAt;
}
