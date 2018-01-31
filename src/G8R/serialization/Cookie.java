/*
 * serialization:Cookie
 * 
 * Date Created: Jan/13/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.serialization;

import java.util.Objects;

public class Cookie implements Comparable<Cookie> {

    private static final String errEmptyString = "empty string";

    private static final String emptyStr = "";
    private static final String delim = "=";


    private String name;
    private String value;

    /**
     * Creates an empty Cookie object
     */
    public Cookie() {
    }

    /**
     * Creates a new Cookie name/value pairing
     * @param nam name to be added
     * @param val value to be associated with the name
     * @throws ValidationException if validation error for name or value
     */
    public Cookie(String nam, String val) throws ValidationException {
        setName(nam);
        setValue(val);
    }

    /**
     * Sets the name of the cookie pairing
     * @param nam name to change to
     * @throws ValidationException if validation error for name
     */
    public void setName(String nam) throws ValidationException {
        if(!emptyStr.equals(nam)) {
            name = nam;
        } else {
            throw new ValidationException(errEmptyString, name);
        }
    }

    /**
     * Sets the value of the cookie pairing
     * @param val value to change value to
     * @throws ValidationException if validation error for value
     */
    public void setValue(String val) throws ValidationException {
        if(!emptyStr.equals(value)) {
            value = val;
        } else {
            throw new ValidationException(errEmptyString, value);
        }
    }

    /**
     * Gets the name of the cookie pairing
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the cookie pairing
     * @return value
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof Cookie)) {
            return false;
        }
        return name.equals((((Cookie) obj).name));
    }

    @Override
    public int compareTo(Cookie o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public String toString(){
        return getName() + delim + getValue();
    }

}
