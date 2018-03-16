/*
 * N4M.serialization:N4MQuery
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.util.Objects;

/**
 * Represents an N4M query and provides serialization/deserialization
 */
public class N4MQuery extends N4MMessage {

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
        setMsgId(msgId);
        setBusinessName(businessName);
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
        if(!businessName.matches(alphaNum)) {
            throw new N4MException(errBusinessName, ErrorCodeType.BADMSG);
        }
        queryBusinessName = Objects.requireNonNull(businessName, errNullName);
    }

    /**
     * Sets error code by number
     * @param errorCodeNum new error code number
     * @throws N4MException if validation fails
     */
    @Override
    public void setErrorCodeNum(int errorCodeNum) throws N4MException {
        super.setErrorCodeNum(errorCodeNum);
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
