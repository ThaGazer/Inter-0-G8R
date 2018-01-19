/*
 * serialization:Cookie
 * 
 * Date Created: Jan/13/2018
 * Author:
 *   -Justin Ritter
 */
package serialization;

import java.util.Objects;

public class Cookie {

    private String name;
    private String value;

    public Cookie() {

    }

    public Cookie(String nam, String val) {
        setName(nam);
        setValue(val);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

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
            return false;
        }
        if(!(obj instanceof Cookie)) {
            return false;
        }
        return name.equals((((Cookie) obj).name));
    }

    @Override
    public String toString(){
        return getName() + "=" + getValue();
    }
}
