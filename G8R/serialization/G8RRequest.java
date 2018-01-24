/*
 * serialization:G8RRequest
 * Created on 1/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package serialization;

/**
 * Represents a G8R request and provides serialization/deserialization
 */
public class G8RRequest extends G8RMessage {

    private String function;
    private String[] params;
    private CookieList cookies;

    /**
     * creates an empty G8R request
     */
    public G8RRequest() {

    }

    /**
     * Creates a new G8R request using given values
     * @param funct request function
     * @param para request parameters
     * @param cook request cookie list
     * @throws ValidationException if error with given values
     * @throws NullPointerException if null parameter
     */
    public G8RRequest(String funct, String[] para, CookieList cook)
            throws ValidationException {

    }

    /**
     * retrun message cookie list
     * @return cookie list
     */
    public CookieList getCookieList() {
        return cookies;
    }

    /**
     * returns function
     * @return function
     */
    public String getFunction() {
        return function;
    }

    /**
     * retruns parameters
     * @return parameters
     */
    public String[] getParams() {
        return params;
    }

    /**
     * set cookie list
     * @param cl cookie list
     * @throws NullPointerException if null cookie list
     */
    public void setCookieList(CookieList cl) {
        cookies = cl;
    }

    /**
     * set function
     * @param funct new function
     * @throws ValidationException if invalid command
     * @throws NullPointerException if null command
     */
    public void setFunction(String funct) throws ValidationException {
        function = funct;
    }

    /**
     * set params
     * @param para new parameters
     * @throws ValidationException if invalid params
     * @throws NullPointerException if null array or array elements
     */
    public void setParams(String[] para) throws ValidationException {
        params = para;
    }

    /**
     * Returns human-readable, complete (i.e., all attribute values),
     * string representation of G8R request message
     * @return string representation
     */
    @Override
    public String toString() {
        return "";
    }
}
