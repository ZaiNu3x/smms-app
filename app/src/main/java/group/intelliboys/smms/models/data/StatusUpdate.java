package group.intelliboys.smms.models.data;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusUpdate {
    private String id;
    private double latitude;
    private double longitude;
    private double altitude;
    private double corneringAngle;
    private int speed;
    private String direction;
    private LocalDateTime createdAt;
    private boolean isWearingHelmet;
    private String travelHistoryId;
}
