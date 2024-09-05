package group.intelliboys.smms.models.results;

public class RegistrationResult {
    private String formId;
    private String message;
    private String status;
    private boolean isEmailExists;
    private boolean isPhoneNumberExists;

    public RegistrationResult() {
    }

    public RegistrationResult(String formId, String message, String status, boolean isEmailExists, boolean isPhoneNumberExists) {
        this.formId = formId;
        this.message = message;
        this.status = status;
        this.isEmailExists = isEmailExists;
        this.isPhoneNumberExists = isPhoneNumberExists;
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

    public boolean isEmailExists() {
        return isEmailExists;
    }

    public void setEmailExists(boolean emailExists) {
        isEmailExists = emailExists;
    }

    public boolean isPhoneNumberExists() {
        return isPhoneNumberExists;
    }

    public void setPhoneNumberExists(boolean phoneNumberExists) {
        isPhoneNumberExists = phoneNumberExists;
    }
}
