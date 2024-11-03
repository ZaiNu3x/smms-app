package group.intelliboys.smms.models.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelHistory {
    private String id;
    private String userId;
    private List<StatusUpdate> statusUpdates;
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
