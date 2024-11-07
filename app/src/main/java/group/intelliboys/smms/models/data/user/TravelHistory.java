package group.intelliboys.smms.models.data.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class TravelHistory {
    private String id;
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
