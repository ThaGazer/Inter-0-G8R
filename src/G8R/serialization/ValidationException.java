/*
 * serialization:ValidationException
 * 
 * Date Created: Jan/11/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.serialization;

import java.io.Serializable;
import java.util.Objects;

public class ValidationException extends Exception implements Serializable {

    private static final String errEmptyTok = "empty token";

    private String token;

    /**
     * Constructs validation exception
     * @param mess exception message
     * @param tok token causing validation failure
     * @throws NullPointerException if null parameters
     */
    public ValidationException(String mess, String tok) {
      this(mess, tok, null);
    }

    /**
     * Constructs validation exception
     * @param message exception message
     * @param token token causing validation failure
     * @param cause exception cause
     * @throws NullPointerException if null parameters
     */
    public ValidationException(String message, String token, Throwable cause) {
        super(message, cause);
        setToken(token);
    }

    /**
     * returns the token of the exception
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * sets the token of the exception
     * @param tok token to change to
     * @throws NullPointerException if tok is null
     */
    private void setToken(String tok) {
        token = Objects.requireNonNull(tok, errEmptyTok);
    }

    public String getReason() {
        return getMessage() + ": " + getToken();
    }

    public void printStackTrace() {
        System.err.println(getToken());
        super.printStackTrace();
    }

    public void printStackTrace(String str) {
        System.err.println(str + ": " + getToken());
        super.printStackTrace();
    }
}
