package group.intelliboys.smms.models.data;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelHistory {
    private String id;
    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startCoordinates;
    private String endCoordinates;
    private String startLocationName;
    private String endLocationName;
    private LocalDateTime createdAt;
    private List<StatusUpdate> statusUpdates;
}
