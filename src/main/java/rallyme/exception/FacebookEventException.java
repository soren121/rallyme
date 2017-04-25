package rallyme.exception;

public class FacebookEventException extends Exception {

    public FacebookEventException(String message) {
        super(message);
    }

    public FacebookEventException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
