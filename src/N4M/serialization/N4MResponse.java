/*
 * N4M.serialization:N4MResponse
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Represents an N4M response and provides serialization/deserialization
 */
public class N4MResponse extends N4MMessage {

    private static final String errTime = "invalid timestamp";

    private Date responseTime;
    private List<ApplicationEntry> responseApplications;

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
