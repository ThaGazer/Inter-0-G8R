/*
 * N4M.serialization:N4MResponse
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.util.*;

/**
 * Represents an N4M response and provides serialization/deserialization
 */
public class N4MResponse extends N4MMessage {

    private static final String errTime = "invalid timestamp";

    private Date responseTime = new Date();
    private List<ApplicationEntry> responseApplications = new ArrayList<>();

    /**
     * Creates new empty N4M response
     */
    public N4MResponse() {}

    /**
     * Creates a new N4M request using given values
     * @param errorCodeNum error number
     * @param msgId message id
     * @param timeStamp timestamp
     * @param applications list of applications
     * @throws N4MException if validation fails
     * @throws NullPointerException if timestamp or applications is null
     */
    public N4MResponse(int errorCodeNum, int msgId, Date timeStamp,
                       List<ApplicationEntry> applications)
            throws N4MException, NullPointerException {
        setErrorCodeNum(errorCodeNum);
        setMsgId(msgId);
        setTimeStamp(timeStamp);
        setApplications(applications);
    }

    public static N4MResponse decode(int errCode, int msgId, byte[] in)
            throws N4MException {

        return new N4MResponse(errCode, msgId, new Date(), new ArrayList<>());
    }

    @Override
    public byte[] encode() {
        return new byte[]{};
    }

    /**
     * Gets list of applications
     * @return list of applications
     */
    public List<ApplicationEntry> getApplications() {
        return (List<ApplicationEntry>)
                Collections.unmodifiableCollection(responseApplications);
    }

    /**
     * Returns timestamp
     * @return timestamp
     */
    public Date getTimeStamp() {
        return (Date) responseTime.clone();
    }

    /**
     * Sets the list of applications
     * @param applications list of applications
     * @throws NullPointerException if applications are null
     */
    public void setApplications(List<ApplicationEntry> applications)
            throws NullPointerException {
        responseApplications.addAll(applications);
    }

    /**
     * Sets the timestamp
     * @param timeStamp response timestamp
     * @throws N4MException if validation fails
     * @throws NullPointerException if timestamp is null
     */
    public void setTimeStamp(Date timeStamp)
            throws N4MException, NullPointerException {
        if(timeStamp.before(new Date(1970))) {
            throw new N4MException(errTime, ErrorCodeType.BADMSG);
        }
        responseTime.setTime(timeStamp.getTime());
    }

    @Override
    public int hashCode() {
        return responseTime.hashCode() + responseApplications.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        N4MResponse that = (N4MResponse)obj;
        return responseApplications.equals(that.responseApplications) &&
            responseTime.equals(that.responseTime);
    }

    @Override
    public String toString() {
        String msg = super.toString() + "Date=" + getTimeStamp() +
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
