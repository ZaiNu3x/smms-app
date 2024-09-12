package group.intelliboys.smms.models.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TwoFAVerificationResult {
    private String formId;
    private String message;
    private String status;
    private String token;
    private boolean isEmailOtpMatches;
    private boolean isSmsOtpMatches;
}
