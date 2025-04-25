package eu.gaiax.model;

/**
 * Result of registration process
 */
public class RegisterResult {

    private boolean errorOccurred;
    private String errorMessage;

    public RegisterResult(boolean errorOccurred, String errorMessage) {
        this.errorOccurred = errorOccurred;
        this.errorMessage = errorMessage;
    }

    public RegisterResult(boolean errorOccurred) {
        this.errorOccurred = errorOccurred;
    }

    public boolean isErrorOccurred() {
        return errorOccurred;
    }

    public RegisterResult setErrorOccurred(boolean errorOccurred) {
        this.errorOccurred = errorOccurred;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public RegisterResult setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
