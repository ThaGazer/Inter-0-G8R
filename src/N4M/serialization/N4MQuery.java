/*
 * N4M.serialization:N4MQuery
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents an N4M query and provides serialization/deserialization
 */
public class N4MQuery extends N4MMessage {

    private static final String errErrCode = "invalid error code number";
    private static final String errBusinessName = "invalid business name";
    private static final String errNullName = "null business name";

    private String queryBusinessName;

    /**
     * Creates an empty N4M query
     */
    public N4MQuery() {}

    /**
     * Creates a new N4M query using given values
     * @param msgId message id
     * @param businessName business name
     * @throws N4MException if validation fails
     * @throws NullPointerException if null message id or null name
     */
    public N4MQuery(int msgId, String businessName)
            throws N4MException, NullPointerException {
        this(msgId, ErrorCodeType.NOERROR, businessName);
    }

    public N4MQuery(int msgId, ErrorCodeType errCode, String businessName)
            throws N4MException, NullPointerException {
        setMsgId(msgId);
        setErrorCode(errCode);
        setBusinessName(businessName);
    }

    /**
     * decodes query message
     * @param msgId message id
     * @param in message byte array
     * @return query message
     * @throws N4MException if validation fails
     */
    public static N4MQuery decode(int msgId, ErrorCodeType errCode, byte[] in)
            throws N4MException {
        if(in == null) {
            throw new NullPointerException(paramByteArr + errNullParam);
        }

        int readPos = 0;

        //length of business name in bytes
        int nameLen = unsignByte(getByte(readPos++, in));

        //bounds check
        if(nameLen+1 != in.length) {
            throw new N4MException(errFrameSize, ErrorCodeType.BADMSGSIZE);
        }

        //business name
        String name = "";
        if(nameLen != 0) {
            name = new String(getBytes(readPos, nameLen, in),
                StandardCharsets.US_ASCII);
        }

        return new N4MQuery(msgId, errCode, name);
    }

    /**
     * encodes query message
     * @return query message
     */
    @Override
    public byte[] encode() {
        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try {
            //message header
            byte[] header = super.encode();

            //set qrCode
            header[0] = (byte) (header[0] & 0xf7);
            ret.write(header);

            //BusinessNameLength
            ret.write((byte) getBusinessName().length());

            //Business name
            ret.write(getBusinessName().getBytes(StandardCharsets.US_ASCII));

            return ret.toByteArray();
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Gets the business name
     * @return business name
     */
    public String getBusinessName() {
        return queryBusinessName;
    }

    /**
     * Sets the business name
     * @param businessName new business name
     * @throws N4MException if validation fails
     * @throws NullPointerException if null name
     */
    public void setBusinessName(String businessName)
            throws N4MException, NullPointerException {
        if(!businessName.matches(chkAllAscii) ||
                businessName.length() > MAXNAME) {
            throw new N4MException(errBusinessName, ErrorCodeType.BADMSG);
        }
        queryBusinessName = Objects.requireNonNull(businessName, errNullName);
    }

    /**
     * Sets error code by number
     * @param ect new error code number
     * @throws N4MException if validation fails
     */
    @Override
    public void setErrorCode(ErrorCodeType ect) throws N4MException {
        if(ect.getErrorCodeNum() != 0) {
            throw new N4MException(errErrCode, ErrorCodeType.BADMSG);
        }
        super.setErrorCode(ect);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + getBusinessName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        N4MQuery that = (N4MQuery)obj;
        return super.equals(that) &&
                getBusinessName().equals(that.getBusinessName());
    }

    @Override
    public String toString() {
        return super.toString() + "BusinessName=" + getBusinessName();
    }
}
