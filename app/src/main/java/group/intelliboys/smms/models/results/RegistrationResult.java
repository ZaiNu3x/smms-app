package group.intelliboys.smms.models.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationResult {
    private String formId;
    private String message;
    private String status;
    private boolean isEmailExists;
    private boolean isPhoneNumberExists;
}
