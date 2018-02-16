/*
 * serialization:G8RMessage
 * Created on 1/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package G8R.serialization;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents generic portion of a G8R message and provides
 * serialization/deserialization.
 */
public abstract class G8RMessage {

    protected static final String errNullMessageOut =
            "Null MessageOutput object";
    protected static final String errNullMessageIn = "Null MessageInput object";
    protected static final String errNullCL = "null cookie list";
    protected static final String errHeader = "Not a G8RMessage header";
    protected static final String errFunction = "not a recognized function";
    protected static final String errType = "Not a G8RMessage type";

    protected static final String val_G8Rheader = "G8R/1.0";
    protected static final String delim_Space = " ";
    protected static final String delim_LineEnd = "\r\n";
    protected static final String alphaNumMore = "[\\w]+";

    protected CookieList cookies;
    protected String function;

    /**
     * Creates a new G8R message by deserialization from the given
     * input according to the specified serialization.
     * @param in user input source
     * @return new G8R message
     * @throws ValidationException if validation fails
     * @throws IOException if I/O problems
     * @throws NullPointerException if in is null
     */
    public static G8RMessage decode(MessageInput in)
            throws ValidationException, IOException {
        if(in.isNull()) {
            throw new NullPointerException(errNullMessageIn);
        }

        String header;
        if(!val_G8Rheader.equals(header = in.readUntil(delim_Space))) {
            throw new ValidationException(errHeader, header);
        }

        String type = in.readUntil(delim_Space);
        switch(type) {
            case "Q":
                return new G8RRequest(in);
            case "R":
                return new G8RResponse(in);
            default:
                throw new ValidationException(errType, type);
        }
    }

    /**
     * encodes the entire G8R message
     * @param out serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    public void encode(MessageOutput out) throws IOException {
        if(out == null) {
            throw new NullPointerException(errNullMessageOut);
        }
        out.write(val_G8Rheader + delim_Space);
    }

    /**
     * return message's cookie list
     * @return cookie list
     */
    public CookieList getCookieList() {
        return cookies;
    }

    /**
     * return message's function
     * @return function
     */
    public String getFunction() {
        return function;
    }

    /**
     * set function
     * @param funct new function
     * @throws ValidationException if invalid command
     * @throws NullPointerException if null command
     */
    public void setFunction(String funct) throws ValidationException {
        if(!funct.matches(alphaNumMore)) {
            throw new ValidationException(errFunction, funct);
        }
        function = Objects.requireNonNull(funct);
    }

    /**
     * set cookie list
     * @param cl cookie list
     * @throws NullPointerException if null cookie list
     */
    public void setCookieList(CookieList cl) {
        cookies = new CookieList(Objects.requireNonNull(cl, errNullCL));
    }

    public String toString() {
        return val_G8Rheader + "=";
    }
}
