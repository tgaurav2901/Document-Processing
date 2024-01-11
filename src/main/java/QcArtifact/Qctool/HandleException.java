package QcArtifact.Qctool;

public class HandleException extends RuntimeException {

    public HandleException(String message) {
        super(message);
    }

    public HandleException(String message, Throwable cause) {
        super(message, cause);
    }
}

