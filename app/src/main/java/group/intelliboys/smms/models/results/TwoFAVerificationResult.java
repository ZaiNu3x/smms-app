package group.intelliboys.smms.models.results;

public class TwoFAVerificationResult {
    private String formId;
    private String message;
    private String status;
    private String token;
    private boolean isEmailOtpMatches;
    private boolean isSmsOtpMatches;

    public TwoFAVerificationResult() {
    }

    public TwoFAVerificationResult(String formId, String message, String status, String token, boolean isEmailOtpMatches, boolean isSmsOtpMatches) {
        this.formId = formId;
        this.message = message;
        this.status = status;
        this.token = token;
        this.isEmailOtpMatches = isEmailOtpMatches;
        this.isSmsOtpMatches = isSmsOtpMatches;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isEmailOtpMatches() {
        return isEmailOtpMatches;
    }

    public void setEmailOtpMatches(boolean emailOtpMatches) {
        isEmailOtpMatches = emailOtpMatches;
    }

    public boolean isSmsOtpMatches() {
        return isSmsOtpMatches;
    }

    public void setSmsOtpMatches(boolean smsOtpMatches) {
        isSmsOtpMatches = smsOtpMatches;
    }

    @Override
    public String toString() {
        return "TwoFAVerificationResult{" +
                "formId='" + formId + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", isEmailOtpMatches=" + isEmailOtpMatches +
                ", isSmsOtpMatches=" + isSmsOtpMatches +
                '}';
    }
}
