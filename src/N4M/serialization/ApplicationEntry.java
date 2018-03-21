/*
 * N4M.serialization:ApplicationEntry
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.util.Objects;

/**
 * Represents one application and its access count
 */
public class ApplicationEntry {

    private static final String errName = "invalid application name";
    private static final String errCount = "invalid application count";

    private final String alphaNum = "[\\w]+";

    private String applicationName;
    private int count;

    /**
     * Creates an empty application entry
     */
    public ApplicationEntry() {}

    /**
     * Creates an application entry
     * @param appName name of the application
     * @throws N4MException if validation fails
     * @throws NullPointerException if null name
     */
    public ApplicationEntry(String appName)
            throws N4MException, NullPointerException {
        this(appName, 0);
    }

    /**
     * Creates an application entry
     * @param appName name of the application
     * @param accessCt application access count
     * @throws N4MException if validation fails
     * @throws NullPointerException if null name
     */
    public ApplicationEntry(String appName, int accessCt)
            throws N4MException, NullPointerException {
        setApplicationName(appName);
        setAccessCount(accessCt);
    }

    /**
     * Returns application access count
     * @return access count
     */
    public int getAccessCount() {
        return count;
    }

    /**
     * Set application access count
     * @param accessCount access count
     * @throws N4MException if validation fails
     */
    public void setAccessCount(int accessCount) throws N4MException {
        if(accessCount < 0) {
            throw new N4MException(errCount, ErrorCodeType.BADMSG);
        }
    }

    /**
     * Returns the application name
     * @return application name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Sets application name
     * @param appName application name
     * @throws N4MException if validation fails
     * @throws NullPointerException if null name
     */
    public void setApplicationName(String appName)
            throws N4MException, NullPointerException {
        if(!appName.matches(alphaNum)) {
            throw new N4MException(errName, ErrorCodeType.BADMSG);
        }
        applicationName = Objects.requireNonNull(appName);
    }

    @Override
    public int hashCode() {
        return getApplicationName().hashCode() +
                Integer.hashCode(getAccessCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationEntry app = (ApplicationEntry)o;
        return getApplicationName().equals(app.getApplicationName())
               && getAccessCount() == app.getAccessCount();
    }

    @Override
    public String toString() {
        return "[" + getApplicationName() + "=" + getAccessCount() + "]";
    }
}
