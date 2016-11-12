package woofworks.exception;

/**
 * Created by Tim on 2016-11-12.
 */
public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
        super("Login attempt is unsuccessful. Please verify your username, password and try again");
    }
}
