package group.intelliboys.smms.models.forms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredential {
    private String token;
    private String deviceId;
    private String deviceName;
}
