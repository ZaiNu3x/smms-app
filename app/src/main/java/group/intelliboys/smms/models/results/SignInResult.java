package group.intelliboys.smms.models.results;

public class SignInResult {
    private String formId;
    private String message;
    private String status;
    private String token;

    public SignInResult() {
    }

    public SignInResult(String formId, String message, String status, String token) {
        this.formId = formId;
        this.message = message;
        this.status = status;
        this.token = token;
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

    @Override
    public String toString() {
        return "SignInResult{" +
                "formId='" + formId + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
