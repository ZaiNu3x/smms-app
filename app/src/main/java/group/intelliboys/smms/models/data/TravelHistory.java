package group.intelliboys.smms.models.data;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
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
    private GeoPoint startLocation;
    private GeoPoint endLocation;
    private LocalDateTime createdAt;
}
