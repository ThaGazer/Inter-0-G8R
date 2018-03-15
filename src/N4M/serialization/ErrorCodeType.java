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
    BM(0), BMS(1), IH(2), NE(3), SR(4);

    public static final ErrorCodeType BADMSG = BM;
    public static final ErrorCodeType BADMSGSIZE = BMS;
    public static final ErrorCodeType INCORRECTHEADER = IH;
    public static final ErrorCodeType NOERROR = NE;
    public static final ErrorCodeType SERVERERROR = SR;

    private static final String errNum = "invalid error code number";

    private int errorCode;
    ErrorCodeType(int code) {
        errorCode = code;
    }
    /**
     * Return errror code corresponding to the error code number
     * @param errorCodeNum error code number to find
     * @return corresponding error code
     * @throws N4MException if invalid error code number
     */
    public static ErrorCodeType valueOf(int errorCodeNum) throws N4MException {
        switch(errorCodeNum) {
            case 0:
                return NOERROR;
            case 1:
                return INCORRECTHEADER;
            case 2:
                return BADMSGSIZE;
            case 3:
                return BADMSG;
            case 4:
                return SERVERERROR;
            default:
                throw new N4MException(errNum, INCORRECTHEADER);
        }
    }

    /**
     * Return error code number corresponding to the error code
     * @return error code number corresponding to the error code
     */
    public int getErrorCodeNum() {
        return errorCode;
    }
}
