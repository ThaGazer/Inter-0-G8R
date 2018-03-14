/*
 * N4M.serialization:N4MException
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

/**
 * N4M validation exception
 */
public class N4MException extends Exception {

    private ErrorCodeType errorCodeType;

    /**
     * Constructs N4M validation exception
     * @param msg exception message
     * @param ect type of error
     */
    public N4MException(String msg, ErrorCodeType ect) {

    }

    /**
     * Constructs N4M validation exception
     * @param msg exceptions message
     * @param ect type of error
     * @param cause exception cause
     * @throws NullPointerException if msg or errorCodeType is null
     */
    public N4MException(String msg, ErrorCodeType ect, Throwable cause)
            throws NullPointerException {

    }

    /**
     * Returns error code type
     * @return error code type
     */
    public ErrorCodeType getErrorCodeType() {
        return errorCodeType;
    }
}
