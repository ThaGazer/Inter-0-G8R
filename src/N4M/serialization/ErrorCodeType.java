/*
 * N4M.serialization:ErrorCodeType
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

/**
 * Error code enumerated type
 */
public enum ErrorCodeType {
    BM, BMS, IH, NE, SR;

    private static final ErrorCodeType BADMSG = BM;
    private static final ErrorCodeType BADMSGSIZE = BMS;
    private static final ErrorCodeType INCORRECTHEADER = IH;
    private static final ErrorCodeType NOERROR = NE;
    private static final ErrorCodeType SERVERERROR = SR;

    /**
     * Return errror code corresponding to the error code number
     * @param errorCodeNum error code number to find
     * @return corresponding error code
     * @throws N4MException if invalid error code number
     */
    public static ErrorCodeType valueOf(int errorCodeNum) throws N4MException {
        return NE;
    }

    /**
     * Return error code number corresponding to the error code
     * @return
     */
    public int getErrorCodeNum() {
        return 0;
    }
}
