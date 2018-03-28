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

    private static String msgWithError = " with error code ";

    private ErrorCodeType errorCodeType;

    /**
     * Constructs N4M validation exception
     * @param msg exception message
     * @param ect type of error
     */
    public N4MException(String msg, ErrorCodeType ect) {
        this(msg, ect, null);
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
        super(msg, cause);
        errorCodeType = ect;
    }

    /**
     * Returns error code type
     * @return error code type
     */
    public ErrorCodeType getErrorCodeType() {
        return errorCodeType;
    }

    /**
     * prints stack trace along with the message and error code of exception
     */
    public void printReason() {
        System.err.println(getMessage() + msgWithError + getErrorCodeType());
        this.printStackTrace();
    }
}
