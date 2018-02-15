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

    private static final String delim = "=";
    private static final String alphaNumMore = "[\\w]+";

    private String name;
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cookie cookie = (Cookie) o;
        return Objects.equals(getName(), cookie.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

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
        if(nam.matches(alphaNumMore)) {
            name = Objects.requireNonNull(nam);
        } else {
            throw new ValidationException(errEmptyString, nam);
        }
    }

    /**
     * Sets the value of the cookie pairing
     * @param val value to change value to
     * @throws ValidationException if validation error for value
     */
    public void setValue(String val) throws ValidationException {
        if(val.matches(alphaNumMore)) {
            value = Objects.requireNonNull(val);
        } else {
            throw new ValidationException(errEmptyString, val);
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
    public int compareTo(Cookie o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public String toString(){
        return getName() + delim + getValue();
    }

}
