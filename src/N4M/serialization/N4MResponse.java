/*
 * N4M.serialization:N4MResponse
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Represents an N4M response and provides serialization/deserialization
 */
public class N4MResponse extends N4MMessage {

    //error messages
    private static final String errTime = "invalid timestamp";
    private static final String errLongTime = "timestamp is too large";
    private static final String errList = "application list is too large";

    //member variables
    private Date responseTime = new Date();
    private List<ApplicationEntry> responseApplications = new ArrayList<>();

    /**
     * Creates new empty N4M response
     */
    public N4MResponse() {
        messageId = 0;
        errorCode = ErrorCodeType.NOERROR;
        responseTime = new Date(0L);
    }

    /**
     * Creates a new N4M request using given values
     * @param ect error number
     * @param msgId message id
     * @param timeStamp timestamp
     * @param applications list of applications
     * @throws N4MException if validation fails
     * @throws NullPointerException if timestamp or applications is null
     */
    public N4MResponse(ErrorCodeType ect, int msgId, long timeStamp,
                       List<ApplicationEntry> applications)
            throws N4MException, NullPointerException {
        setErrorCode(ect);
        setMsgId(msgId);
        setTimestamp(timeStamp);
        setApplications(applications);
    }

    /**
     * decodes response message
     * @param errCode error code
     * @param msgId message id
     * @param in message byte array
     * @return response message
     * @throws N4MException if validations fails
     */
    public static N4MResponse decode(ErrorCodeType errCode,
                                     int msgId, byte[] in)
            throws N4MException {
        int readPos = 0;

        //timestamp
        long time = b2i(getBytes(readPos, 4, in));
        readPos += 4;

        int appCount = unsignByte(getByte(readPos++, in));

        List<ApplicationEntry> entries = new ArrayList<>();
        for(int i = 0; i < appCount; i++) {
            //application use count
            int count = (int) b2i(getBytes(readPos, 2, in));
            readPos += 2;

            //length of application name in bytes
            int nameLen = unsignByte(getByte(readPos++, in));

            //application name
            String name = "";
            if(nameLen != 0) {
                name = new String(getBytes(readPos, nameLen, in),
                        StandardCharsets.US_ASCII);
            }
            readPos += nameLen;

            entries.add(new ApplicationEntry(name, count));
        }

        return new N4MResponse(errCode, msgId, time, entries);
    }

    /**
     * encodes response message
     * @return response message or null if buffer problem
     */
    @Override
    public byte[] encode() {
        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try {
            //message header
            byte[] header = super.encode();

            //set qrCode
            header[0] |= (0x08);

            //set errorCode
            header[0] |= (getErrorCode().getErrorCodeNum() & 0x07);

            ret.write(header);

            //Timestamp
            int i = (int) (getTimestamp());
            ret.write(i2b(i));

            //ApplicationsCount
            ret.write((byte) getApplications().size());

            for(ApplicationEntry ae : getApplications()) {
                //Application Entries
                ret.write(ae.encode());
            }

            return ret.toByteArray();
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Gets list of applications
     * @return list of applications
     */
    public List<ApplicationEntry> getApplications() {
        return Collections.unmodifiableList(responseApplications);
    }

    /**
     * Returns timestamp in milliseconds
     * @return timestamp
     */
    public long getTimestamp() {
        return responseTime.getTime()/1000;
    }

    /**
     * Sets the list of applications
     * @param applications list of applications
     * @throws NullPointerException if applications are null
     */
    public void setApplications(List<ApplicationEntry> applications)
            throws NullPointerException, N4MException {
        if(applications.size() > MAXID) {
            throw new N4MException(errList, ErrorCodeType.BADMSG);
        }
        responseApplications.addAll(applications);
    }

    /**
     * Sets the timestamp
     * @param timeStamp response timestamp in seconds
     * @throws N4MException if validation fails
     * @throws NullPointerException if timestamp is null
     */
    public void setTimestamp(long timeStamp)
            throws N4MException, NullPointerException {
        if(timeStamp > 0xffffffffL) {
            throw new N4MException(errLongTime, ErrorCodeType.BADMSG);
        }

        long timestamp_Milli = TimeUnit.SECONDS.toMillis(timeStamp);
        Date timeCheck = new Date(timestamp_Milli);
        if(timeCheck.before(Date.from(Instant.EPOCH))) {
            throw new N4MException(errTime, ErrorCodeType.BADMSG);
        }
        responseTime.setTime(timestamp_Milli);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + responseTime.hashCode() +
                responseApplications.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        N4MResponse that = (N4MResponse)obj;
        return super.equals(that) &&
                responseApplications.equals(that.responseApplications) &&
                responseTime.equals(that.responseTime);
    }

    @Override
    public String toString() {
        String msg = super.toString() + "Date=" + getTimestamp() +
                "Applications=";

        boolean first = true;
        for(ApplicationEntry ae : getApplications()) {
            if(first) {
                msg += ae.toString();
                first = false;
            } else {
                msg += ", " + ae.toString();
            }
        }
        return msg;
    }
}
