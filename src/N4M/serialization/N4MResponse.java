/*
 * N4M.serialization:N4MResponse
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an N4M response and provides serialization/deserialization
 */
public class N4MResponse extends N4MMessage {

    /**
     * Creates new empty N4M response
     */
    public N4MResponse() {

    }

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

    }

    /**
     * Gets list of applications
     * @return list of applications
     */
    public List<ApplicationEntry> getApplications() {
        return new ArrayList<>();
    }

    /**
     * Returns timestamp
     * @return timestamp
     */
    public Date getTimeStamp() {
        return new Date();
    }

    /**
     * Sets the list of applications
     * @param applications list of applications
     * @throws NullPointerException if applications are null
     */
    public void setApplications(List<ApplicationEntry> applications)
            throws NullPointerException {

    }

    /**
     * Sets the timestamp
     * @param timeStamp response timestamp
     * @throws N4MException if validation fails
     * @throws NullPointerException if timestamp is null
     */
    public void setTimeStamp(Date timeStamp)
            throws N4MException, NullPointerException {

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
