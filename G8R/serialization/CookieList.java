/*
 * serialization:CookieList
 * 
 * Date Created: Jan/11/2018
 * Author:
 *   -Justin Ritter
 */
package serialization;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * List of cookie(name/value) pairs
 */
public class CookieList {

    private static final String errNullCookie = "empty cookie object";
    private static final String errNullMessageOut = "Null MessageOutput object";
    private static final String errCookieFormat =
            "incorrect serialization of cookie";

    private static final String delim_LineEnding = "\r\n";
    private static final String delim_NameValue = "=";
    private static final String delim_Cookie = "\n";
    private static final String emptyStr = "";

    private Set<Cookie> cookieList;

    /**
     * Creates a new, empty cookie list
     */
    public CookieList() {
        cookieList = new TreeSet<>();
    }

    /**
     * Creates a new, cloned cookie list
     * @param cl list of cookies to clone
     */
    public CookieList(CookieList cl) {
        cookieList = new TreeSet<>();
        cookieList.addAll(cl.cookieList);
    }

    /**
     * Creates a new CookieList by decoding the input stream
     * @param in input stream from which to deserialize the name/value list
     * @throws ValidationException if validation problem such as illegal name
     * and/or value, ect.
     * @throws IOException if I/O problem (EOFException for EoS)
     * @throws NullPointerException if input stream is null
     */
    public CookieList(MessageInput in)
            throws ValidationException, IOException {
        cookieList = new TreeSet<>();

        String word;
        if(!in.isNull()) {
            while (!emptyStr.equals(word = in.readUntil(delim_Cookie))) {
                Cookie newCookie = new Cookie();
                String[] words = word.split(delim_NameValue);
                if (words.length == 2) {
                    newCookie.setName(words[0]);
                    newCookie.setValue(words[1]);
                    cookieList.add(newCookie);
                } else {
                    throw new ValidationException(errCookieFormat, word);
                }
            }
        }
    }

    /**
     * Creates a new CookieList by decoding from the console
     * @param in console input source
     * @param out prompt output sink
     * @throws NullPointerException if in or out is null
     */
    public CookieList(Scanner in, PrintStream out) {
    }

    /**
     * Gets the set of names
     * @return Set (potentially empty) of names (strings) for this list
     */
    public Set<String> getNames() {
        Set<String> names = new TreeSet<>();

        for(Cookie c : cookieList) {
            names.add(c.getName());
        }
        return names;
    }

    /**
     * Gets the value associated with the given name
     * @param name cookie name
     * @return value associated with the given name or null if no such name
     */
    public String getValue(String name) {
        if(name != null && !emptyStr.equals(name)) {
            for (Cookie c : cookieList) {
                if (c.getName().equals(name)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Encode the name-value list. The name-value pair serialization must be
     * in sort order (alphabetically by name in increasing order)
     * @param out serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    public void encode(MessageOutput out) throws IOException {
        if(!out.isNull()) {
            for (Cookie c : cookieList) {
                out.write(c.toString() + delim_LineEnding);
            }
            out.write(delim_LineEnding);
        } else {
            throw new NullPointerException(errNullMessageOut);
        }
    }

    /**
     * Adds the name/value pair. If the name already exists, the new value
     * replaces the old value
     * @param nam name to be added
     * @param val value to be associated with the name
     * @throws ValidationException if validation failure for name or value
     * @throws NullPointerException if name or value is null
     */
    public void add(String nam, String val) throws ValidationException {
        add(new Cookie(nam, val));
    }

    /**
     * Adds the cookie. If the name already exists, the new value
     * replaces the old value
     * @param c cookie to add
     * @throws NullPointerException if cookie is null
     */
    public void add(Cookie c) {
        c = Objects.requireNonNull(c, errNullCookie);
        if(contains(c.getName())) {
            remove(c);
            cookieList.add(c);
        } else {
            cookieList.add(c);
        }
    }

    /**
     * removes a cookie from the list
     * @param nam name of cookie
     * @return if cookie was successfully removed
     * @throws ValidationException if cookie issues
     * @throws NullPointerException if name is null
     */
    public boolean remove(String nam) throws ValidationException {
        return remove(new Cookie(nam, ""));
    }

    /**
     * removes a cookie from the list
     * @param c cookie to remove
     * @return if cookie was successfully removed
     * @throws NullPointerException if null cookie
     */
    public boolean remove(Cookie c) {
        c = Objects.requireNonNull(c);

        return cookieList.remove(c);
    }

    /**
     * check if the cookie list contains a name
     * @param name name of cookie to search for
     * @return true if cookie is found
     */
    public boolean contains(String name) {
        return getValue(name) != null;
    }

    /**
     * Gives the current size of the cookie list
     * @return the size of the list
     */
    public int size() {
        return cookieList.size();
    }

    @Override
    public int hashCode() {
        return cookieList.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                obj instanceof CookieList && cookieList.equals(obj);
    }

    /**
     * Returns string representation of cookie list. The name-value pair
     * serialization must be in sort order
     * (alphabetically by nae in increasing order).
     * Cookies=[a=1,b=2]
     * @return string representation of cooke list
     */
    @Override
    public String toString() {
        String objStr = "Cookies=[";
        boolean firstCookie = true;

        for(Cookie c : cookieList) {
            if(!firstCookie) {
                objStr += ",";
            }
            objStr += c.toString();
            firstCookie = false;
        }
        objStr += "]";
        return objStr;
    }
}
