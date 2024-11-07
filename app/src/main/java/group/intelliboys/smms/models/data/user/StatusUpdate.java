package group.intelliboys.smms.models.data.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusUpdate {
    private String id;
    private float latitude;
    private float longitude;
    private float altitude;
    private float corneringAngle;
    private int speed;
    private String direction;
    private boolean isWearingHelmet;
    private LocalDateTime createdAt;
}
