package rallyme.exception;

public class RallyException extends Exception {

    public RallyException(String message) {
        super(message);
    }

    public RallyException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
