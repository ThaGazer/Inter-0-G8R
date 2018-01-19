/*
 * serialization:ValidationException
 * 
 * Date Created: Jan/11/2018
 * Author:
 *   -Justin Ritter
 */
package serialization;

import java.io.Serializable;

public class ValidationException extends Exception implements Serializable {

    private String token;

    /**
     * Constructs validation exception
     * @param mess exception message
     * @param tok token causing validation failure
     * @throws NullPointerException if null parameters
     */
    public ValidationException(String mess, String tok) {
        super(mess);
        token = tok;
    }

    /**
     * Constructs validation exception
     * @param mess exception
     * @param tok token causing validation failure
     * @param caus exception cause
     * @throws NullPointerException if null parameters
     */
    public ValidationException(String mess, String tok, Throwable caus) {
        super(mess, caus);
        token = tok;
    }

    /**
     * returns the token of the exception
     * @return the token
     */
    public String getToken() {
        return token;
    }
}
