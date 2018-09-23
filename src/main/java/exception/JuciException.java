package exception;

public class JuciException extends Exception {
    public JuciException() {
    }

    public JuciException(String message) {
        super(message);
    }

    public JuciException(Throwable cause) {
        super(cause);
    }

    public JuciException(String message, Throwable cause) {
        super(message, cause);
    }
}
