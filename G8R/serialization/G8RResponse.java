/*
 * serialization:G8RResponse
 * Created on 1/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package serialization;

import java.io.IOException;
import java.util.Objects;

public class G8RResponse extends G8RMessage {

    private static final String errNullCL = "null cookie list";
    private static final String errNullFunct = "null function";
    private static final String errNullMessage = "null message";
    private static final String errStatus = "not an G8RResponse status";

    private static final String val_Type = "R";
    private static final String type_OK = "OK";
    private static final String type_ERROR = "ERROR";

    private String status;
    private String function;
    private String message;
    private CookieList cookieList;

    /**
     * creates an empty G8RResponse message
     */
    public G8RResponse() {
    }

    public G8RResponse(MessageInput in)
            throws IOException, ValidationException {
        if(in.isNull()) {
            throw new NullPointerException(errNullMessageIn);
        }

        //reads in status
        setStatus(in.readUntil(delim_Space));

        //reads in function
        setFunction(in.readUntil(delim_Space));

        //reads in message
        setMessage(in.readUntil(delim_Space));

        //reads in cookielist
        setCookieList(new CookieList(in));
    }

    /**
     * Constructs G8R response using given values
     * @param stat response status
     * @param funct response function
     * @param mess response message
     * @param cl response cookie list
     * @throws ValidationException if error with given values
     * @throws NullPointerException if null parameters
     */
    public G8RResponse(String stat, String funct, String mess, CookieList cl)
            throws ValidationException {
        setStatus(stat);
        setFunction(funct);
        setMessage(mess);
        setCookieList(cl);
    }

    /**
     * encodes a G8RResponse message
     * @param out serialization output sink
     * @throws IOException if write problem
     */
    public void encode(MessageOutput out) throws IOException {
        if(out.isNull()) {
            throw new NullPointerException(errNullMessageOut);
        }

        //writes message header
        super.encode(out);

        //writes out message type
        out.write(val_Type + delim_Space);

        //writes out message status
        out.write(getStatus() + delim_Space);

        //writes out message function
        out.write(getFunction() + delim_Space);

        //writes out messages' message
        out.write(getMessage() + lineEnd);

        //writes out messages' cookielist
        getCookieList().encode(out);
    }

    /**
     * return cookie list
     * @return cookie list
     */
    public CookieList getCookieList() {
        return cookieList;
    }

    /**
     * return function
     * @return function
     */
    public String getFunction() {
        return function;
    }

    /**
     * return message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * return status
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * set cookie list
     * @param cl cookie list
     * @throws NullPointerException if null cookie list
     */
    public void setCookieList(CookieList cl) {
        cookieList = Objects.requireNonNull(cl, errNullCL);
    }

    /**
     * set function
     * @param funct mew function
     * @throws ValidationException if invalid command
     * @throws NullPointerException if null command
     */
    public void setFunction(String funct) throws ValidationException {
        function = Objects.requireNonNull(funct, errNullFunct);
    }

    /**
     * set message
     * @param mess new message
     * @throws ValidationException if invalid message
     * @throws NullPointerException if null message
     */
    public void setMessage(String mess) throws ValidationException {
        message = Objects.requireNonNull(mess, errNullMessage);
    }

    /**
     * set status
     * @param stat new status
     * @throws ValidationException if invalid status
     * @throws NullPointerException if null status
     */
    public void setStatus(String stat) throws ValidationException {
        if(!type_OK.equals(stat) && !type_ERROR.equals(stat)) {
            throw new ValidationException(errStatus, stat);
        }
        status = Objects.requireNonNull(stat);
    }

    /**
     * retruns human-readable string repressentation of G8R response message
     * @return string representation
     */
    public String toString() {
        return super.toString() + val_Type + "Status=" + getStatus() +
                "Function=" + getFunction() + "Message=" + getMessage() +
                getCookieList().toString();
    }
}
