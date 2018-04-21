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
    protected static final String errNullError = "null error code";

    //parameter types
    protected static final String paramByteArr = "byte array";

    //packet masks
    private static final int messageHeaderLen = 2;
    private static final byte versionMask = (byte)0xf0;
    private static final byte qrMask = 0x08;
    private static final byte errCodeMask = 0x07;

    //max variables
    protected static final int MAXID = (int)Math.pow(2, 8)-1;
    protected static final int MAXNAME = (int)Math.pow(2, 8)-1;

    //String checks
    protected final String chkAllAscii = "[ -~]*";

    //member variables
    private static final byte version = 0x02;
    protected ErrorCodeType errorCode;
    protected int messageId;

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
                return N4MQuery.decode(msgId, ErrorCodeType.valueOf(errCode),
                        getBytes(readPos, in.length-2, in));
            case 1:
                return N4MResponse.decode(ErrorCodeType.valueOf(errCode), msgId,
                        getBytes(readPos, in.length-2, in));
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
        ret[pos] = (byte) (ret[pos] | getErrorCode().getErrorCodeNum());

        ret[++pos] = (byte) getMsgId();
        return ret;
    }

    /**
     * Returns error code number
     * @return error code number
     */
    public ErrorCodeType getErrorCode() {
        return errorCode;
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
     * @param ect new error code number
     * @throws N4MException if validation fails
     */
    public void setErrorCode(ErrorCodeType ect) throws N4MException {
        if(ect == null) {
            throw new NullPointerException(errNullError);
        }
        errorCode = ect;
    }

    /**
     * Set the message id
     * @param msgId new message id
     * @throws N4MException if validation fails
     */
    public void setMsgId(int msgId) throws N4MException {
        if(msgId < 0 || msgId > MAXID) {
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
        return getBytes(pos, 0, bArr)[0];
    }

    /**
     * creates a new byte array of size end-start
     * @param offSet starting position to read from
     * @param length amount to read
     * @param bArr original array
     * @return new byte array
     * @throws N4MException params are out of bounds of the byte array
     */
    protected static byte[] getBytes(int offSet, int length, byte[] bArr)
            throws N4MException {
        if(bArr == null) {
            throw new NullPointerException(paramByteArr + errNullParam);
        }
        if(offSet < 0 || length < 0) {
            throw new IllegalArgumentException(errGetByteParams);
        }
        if(offSet >= bArr.length || length+offSet > bArr.length) {
            throw new N4MException(errFrameSize, ErrorCodeType.BADMSGSIZE);
        }
        if(length == 0) {
            return new byte[]{bArr[offSet]};
        }

        byte[] ret = new byte[length];
        int j = 0;
        for(int i = offSet; i < length+offSet; i++, j++) {
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
            integer <<= 8;
            integer |= unsignByte(bInt[i]);
        }
        return integer;
    }

    /**
     * converts integer to a 4 byte array
     * @param val integer to convert
     * @return byte array representation of val
     */
    protected  static byte[] i2b(long val) {
        byte[] ret = new byte[4];

        for(int i = 3; i >= 0; i--) {
            ret[i] = (byte)(val & 0xff);
            val >>= 8;
        }

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
                getErrorCode() == that.getErrorCode();
    }

    @Override
    public String toString() {
        return "ID=" + getMsgId() + "ErrorCode=" + getErrorCode();
    }
}
