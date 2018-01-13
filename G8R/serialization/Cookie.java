/*
 * serialization:Cookie
 * 
 * Date Created: Jan/13/2018
 * Author:
 *   -Justin Ritter
 */
package serialization;

public class Cookie {

    private String name;
    private String value;

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
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString(){
        return "";
    }
}
