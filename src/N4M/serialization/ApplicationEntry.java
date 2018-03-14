/*
 * N4M.serialization:ApplicationEntry
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

/**
 * Represents one application and its access count
 */
public class ApplicationEntry {

    private String applicationName;
    private int count;

    /**
     * Creates an empty application entry
     */
    public ApplicationEntry() {

    }

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
     * @params access count
     * @throws N4MException if validation fails
     */
    public void setAccessCount(int accessCount) throws N4MException {

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

    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public String toString() {
        return "";
    }
}
