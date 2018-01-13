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

    /**
     * Constructs validation exception
     * @param message exception message
     * @param token token causing validation failure
     * @throws NullPointerException if null parameters
     */
    public ValidationException(String message, String token) {

    }

    /**
     * Constructs validation exception
     * @param message exception
     * @param token token causing validation failure
     * @param cause exception cause
     * @throws NullPointerException if null parameters
     */
    public ValidationException(String message, String token, Throwable cause) {

    }

    /**
     * returns the token of the exception
     * @return the token
     */
    public String getToken() {
        return "";
    }
}
