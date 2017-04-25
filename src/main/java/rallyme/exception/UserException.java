/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.exception.UserException
 */

package rallyme.exception;

public class UserException extends Exception {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
