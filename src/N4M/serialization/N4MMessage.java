/*
 * N4M.serialization:N4MMessage
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

/**
 * Represents generic portion of a N4M message and provides
 * serialization/deserialization
 */
public class N4MMessage {

    private static final String errMessID = "invalid message ID";

    public static final String alphaNum = "[\\w]+";

    private ErrorCodeType errorCode;
    private int messageId;

    /**
     * Creates a new N4M message by deserializing from the given byte array
     * according to the specified serialization.
     * @param in buffer of received packet
     * @return new N4M message
     * @throws N4MException if validation fails
     * @throws NullPointerException if in is null
     */
    public static N4MMessage decode(byte[] in)
            throws N4MException, NullPointerException {
        return new N4MMessage();
    }

    /**
     * Return human-readable representation
     * @return human-readable string
     */
    public byte[] encode() {
        return new byte[]{};
    }

    /**
     * Returns error code number
     * @return error code number
     */
    public int getErrorCodeNum() {
        return errorCode.getErrorCodeNum();
    }

    /**
     * Returns message id
     * @return message id
     */
    public int getMsgId() {
        return messageId;
    }

    /**
     * Set error code by number
     * @param errorCodeNum new error code number
     * @throws N4MException if validation fails
     */
    public void setErrorCodeNum(int errorCodeNum) throws N4MException {
        errorCode = ErrorCodeType.valueOf(errorCodeNum);
    }

    /**
     * Set the message id
     * @param msgId new message id
     * @throws if validation fails
     */
    public void setMsgId(int msgId) throws N4MException {
        if(msgId < 0) {
            throw new N4MException(errMessID, ErrorCodeType.BADMSG);
        }
        messageId = msgId;
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object obj) {
        return false;
    }

    public String toString() {
        return "";
    }
}
