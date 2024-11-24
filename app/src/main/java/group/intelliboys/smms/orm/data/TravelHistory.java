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
@Entity(tableName = "travel_history",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "email",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
@JsonIgnoreProperties(ignoreUnknown = true)
public class TravelHistory implements Serializable {
    @NonNull
    @PrimaryKey
    private String travelHistoryId;
    private String userId;
    private boolean isSync;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private float startLatitude;
    private float startLongitude;
    private float startAltitude;
    private String startLocationName;
    private float endLatitude;
    private float endLongitude;
    private float endAltitude;
    private String endLocationName;
    private LocalDateTime createdAt;
}
