/*
 * serialization:G8RRequest
 * Created on 1/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package G8R.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a G8R request and provides serialization/deserialization
 */
public class G8RRequest extends G8RMessage {

    private static final String errCommand = "not a command";
    private static final String errParameter = "improper parameter format";
    private static final String errNullParam = "null parameter";

    private static final String val_Command = "RUN";
    private static final String val_Type = "Q";

    private String[] params;

    /**
     * creates an empty G8R request
     */
    public G8RRequest() {
    }

    /**
     * Creates a new G8RR request using a MessageInput stream
     * @param in input sink
     * @throws ValidationException if error with given parameters
     * @throws IOException if I/O problem
     * @throws NullPointerException if null parameter
     */
    public G8RRequest(MessageInput in) throws IOException, ValidationException {
        if(in.isNull()) {
            throw new NullPointerException(errNullMessageIn);
        }

        String[] req = in.readUntil(delim_LineEnd).split(delim_Space);

        List<String> params = new ArrayList<>();
        for(int i = 0; i < req.length; i++) {
            switch(i) {
                case 0:
                    if(!val_Command.equals(req[i])) {
                        throw new ValidationException(errCommand, req[i]);
                    }
                    break;
                case 1:
                    setFunction(req[i]);
                    break;
                default:
                    params.add(req[i]);
            }
        }

        if(!params.isEmpty()) {
            setParams(params.toArray(new String[0]));
        } else {
            setParams(new String[]{});
        }

        //reads in cookies
        setCookieList(new CookieList(in));
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
        super();
        setFunction(funct);
        setParams(para);
        setCookieList(cook);
    }

    /**
     * encodes a G8RRequest to the output sink
     * @param out serialization output sink
     * @throws IOException if write issues
     */
    public void encode(MessageOutput out) throws IOException {
        if(out.isNull()) {
            throw new NullPointerException(errNullMessageOut);
        }

        //writes header
        super.encode(out);

        //writes type of message
        out.write(val_Type + delim_Space);

        //writes the command
        out.write(val_Command + delim_Space);

        //writes the message function
        out.write(function);

        //writes out all parameters
        if(getParams() != null) {
            for (String s : params) {
                out.write(delim_Space + s);
            }
        }
        out.write(delim_LineEnd);

        //writes out the cookie list
        cookies.encode(out);
    }

    /**
     * returns parameters
     * @return parameters
     */
    public String[] getParams() {
        return Arrays.copyOf(params, params.length);
    }

    /**
     * set params
     * @param para new parameters
     * @throws ValidationException if invalid params
     * @throws NullPointerException if null array or array elements
     */
    public void setParams(String[] para) throws ValidationException {
        Objects.requireNonNull(para, errNullParam);
        for (String s : para) {
            if (!s.matches(alphaNumMore)) {
                throw new ValidationException(errParameter,
                        Arrays.toString(para));
            }
        }
        params = Arrays.copyOf(para, para.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        G8RRequest that = (G8RRequest) o;
        return Objects.equals(getFunction(), that.getFunction()) &&
                Arrays.equals(getParams(), that.getParams()) &&
                Objects.equals(cookies, that.cookies);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getFunction(), cookies);
        result = 31 * result + Arrays.hashCode(getParams());
        return result;
    }

    /**
     * Returns human-readable, complete (i.e., all attribute values),
     * string representation of G8R request message
     * @return string representation
     */
    @Override
    public String toString() {
        return super.toString() + val_Type + "Function=" + getFunction() +
                "Parameters=" + Arrays.toString(getParams()) +
                getCookieList().toString();
    }
}
