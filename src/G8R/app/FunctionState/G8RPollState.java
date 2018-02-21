/*
 * G8R.app:G8RPollState
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

public enum G8RPollState {
    POLL("Poll"), NAMESTEP("NameStep"), FOODMOOD("FoodMood");

    private String name;

    G8RPollState(String str) {
        name = str;
    }

    public String getName() {
        return name;
    }
}
