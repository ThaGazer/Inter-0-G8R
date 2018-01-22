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
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * List of cookie(name/value) pairs
 */
public class CookieList {

    private static final String errDoubleEql = "double equals";

    private static final String lineEnding = "\r\n";

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
        if(in.isNull()) {
            while (!"\n".equals(word = in.readUntil())) {
                Cookie newCookie = new Cookie();
                String[] words = word.split("=");
                if (words.length == 2) {
                    newCookie.setName(words[0]);
                    newCookie.setValue(words[1]);
                    cookieList.add(newCookie);
                } else {
                    throw new ValidationException(errDoubleEql, word);
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
        Set<String> names = new HashSet<>();

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
        if(name != null && !name.equals("")) {
            for (Cookie c : cookieList) {
                if (c.getName().equals(name)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Encode the name-value list. The name-value pair serizlization must be
     * in sort order (alphabetically by name in increasing order)
     * @param out serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    public void encode(MessageOutput out) throws IOException {
        String encodeMsg = "";

        for(Cookie c : cookieList) {
            encodeMsg += c.toString();
            encodeMsg += lineEnding;
        }
        encodeMsg += lineEnding;
        out.write(encodeMsg);
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
        Cookie newCookie = new Cookie(nam, val);
        cookieList.add(newCookie);
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
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof CookieList)) {
            return false;
        }
        return cookieList.equals(obj);
    }

    /**
     * Returns string representation of cookie list. The name-value pair
     * serialization must be in sort order
     * (alphabetically by nae in increaseing order).
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
