/*
 * N4M.serialization:ApplicationEntry
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static N4M.serialization.N4MMessage.*;

/**
 * Represents one application and its access count
 */
public class ApplicationEntry {

    //error messages
    private static final String errName = "invalid application name";
    private static final String errCount = "invalid application count";

    //string parsing
    private final String alphaNum = "[\\w]+";

    //member variables
    private String applicationName;
    private int accessCount = 0;

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
     * encodes application entry
     * @return application entry
     */
    public byte[] encode() {
        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try {
            byte[] countByte = i2b(getAccessCount());

            ret.write(Arrays.copyOfRange(countByte, 2, countByte.length));
            ret.write((byte) getApplicationName().length());
            ret.write(getApplicationName().getBytes(StandardCharsets.US_ASCII));

            return ret.toByteArray();
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Returns application access count
     * @return access count
     */
    public int getAccessCount() {
        return accessCount;
    }

    /**
     * Set application access count
     * @param count access count
     * @throws N4MException if validation fails
     */
    public void setAccessCount(int count) throws N4MException {
        if(count < 0) {
            throw new N4MException(errCount, ErrorCodeType.BADMSG);
        }
        accessCount = count;
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
