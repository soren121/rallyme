/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.exception.RallyException
 */

package rallyme.exception;

public class RallyException extends Exception {

    public RallyException(String message) {
        super(message);
    }

    public RallyException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
