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

    //error messages
    private static final String errMessID = "invalid message ID";
    private static final String errGetByteParams = "invalid getByte parameters";
    private static final String errVersion = "incorrect version";
    private static final String errMsgType = "unexpected message type";
    protected static final String errFrameSize = "incorrect frame sizing";
    protected static final String errNullParam = " cannot be null";

    //parameter types
    protected static final String paramByteArr = "byte array";

    //string parsing
    protected static final String alphaNum = "[\\w]+";

    //packet masks
    private static final int messageHeaderLen = 2;
    private static final byte versionMask = (byte)0xf0;
    private static final byte qrMask = 0x08;
    private static final byte errCodeMask = 0x07;

    //member variables
    private static final byte version = 0x02;
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
        if(in == null) {
            throw new NullPointerException(paramByteArr + errNullParam);
        }
        if(in.length < messageHeaderLen) {
            throw new N4MException(errFrameSize, ErrorCodeType.BADMSGSIZE);
        }

        int readPos = 0;
        byte b = getByte(readPos++, in);

        //version
        if((b & versionMask) >> 4 != version) {
            throw new N4MException(errVersion, ErrorCodeType.INCORRECTHEADER);
        }

        //QR
        int qrCode = (b & qrMask) >> 3;

        //errCode
        int errCode = b & errCodeMask;

        //MsgId
        int msgId = unsignByte(getByte(readPos++, in));

        switch(qrCode) {
            case 0:
                return N4MQuery.decode(msgId, getBytes(readPos, in.length, in));
            case 1:
                return N4MResponse.decode(errCode, msgId,
                        getBytes(readPos, in.length, in));
            default:
                throw new N4MException(errMsgType,
                        ErrorCodeType.INCORRECTHEADER);
        }
    }

    /**
     * Return human-readable representation
     * @return human-readable string
     */
    public byte[] encode() {
        byte[] ret = new byte[messageHeaderLen];
        int pos = 0;

        ret[pos] = version << 4;
        ret[pos] = (byte) (ret[pos] | getErrorCodeNum());

        ret[++pos] = (byte) getMsgId();
        return ret;
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
     * @throws N4MException if validation fails
     */
    public void setMsgId(int msgId) throws N4MException {
        if(msgId < 0) {
            throw new N4MException(errMessID, ErrorCodeType.BADMSG);
        }
        messageId = msgId;
    }

    /**
     * a single byte from the byte array
     * @param pos position of byte
     * @param bArr original byte array
     * @return byte at pos in byte array
     * @throws N4MException params are out of bounds of the byte array
     */
    protected static byte getByte(int pos, byte[] bArr)
            throws N4MException {
        return getBytes(pos, pos, bArr)[0];
    }

    /**
     * creates a new byte array of size end-start
     * @param start starting position of new array
     * @param end end position of new array
     * @param bArr original array
     * @return new byte array
     * @throws N4MException params are out of bounds of the byte array
     */
    protected static byte[] getBytes(int start, int end, byte[] bArr)
            throws N4MException {
        if(bArr == null) {
            throw new NullPointerException(paramByteArr + errNullParam);
        }
        if(start < 0 || end < start) {
            throw new IllegalArgumentException(errGetByteParams);
        }
        if(end > bArr.length) {
            throw new N4MException(errFrameSize, ErrorCodeType.BADMSGSIZE);
        }
        if(start == end) {
            return new byte[]{bArr[start]};
        }

        byte[] ret = new byte[end-start];
        int j = 0;
        for(int i = start; i < end; i++, j++) {
            ret[j] = bArr[i];
        }
        return ret;
    }

    /**
     * clears most significant bits of the byte
     * @param b byte to convert to unsigned
     * @return int representation of unsigned byte
     */
    protected static int unsignByte(byte b) {
        return (b & 0xff);
    }

    /**
     * converts varying byte array to long
     * @param bInt byte array to convert
     * @return long representation of byte array
     */
    protected static long b2i(byte[] bInt) {
        if(bInt == null) {
            throw new NullPointerException(paramByteArr + errNullParam);
        }

        long integer = 0;
        for(int i = 0; i < bInt.length; i++) {
            integer += unsignByte(bInt[i]) << 8*(bInt.length-i-1);
        }
        return integer;
    }

    /**
     * converts integer to a 4 byte array
     * @param val integer to convert
     * @return byte array representation of val
     */
    protected  static byte[] i2b(int val) {
        byte[] ret = new byte[4];

        ret[3] = (byte) (val & 0xff);
        ret[2] = (byte) ((val >> 8) & 0xff);
        ret[1] = (byte) ((val >> 16) & 0xff);
        ret[0] = (byte) ((val >> 24) & 0xff);

        return ret;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getMsgId()) + errorCode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        N4MMessage that = (N4MMessage)obj;
        return getMsgId() == that.getMsgId() &&
                getErrorCodeNum() == that.getErrorCodeNum();
    }

    @Override
    public String toString() {
        return "ID=" + getMsgId() + "ErrorCode=" + getErrorCodeNum();
    }
}
