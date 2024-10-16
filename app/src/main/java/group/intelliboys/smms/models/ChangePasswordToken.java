package group.intelliboys.smms.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordToken {
    private String id;
    private String emailOtp;
    private String smsOtp;
}
