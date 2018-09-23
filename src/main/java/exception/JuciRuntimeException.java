package exception;

public class JuciRuntimeException extends RuntimeException {
    public JuciRuntimeException() {
    }

    public JuciRuntimeException(String message) {
        super(message);
    }

    public JuciRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JuciRuntimeException(Throwable cause) {
        super(cause);
    }
}
