/*
 * G8R.app.FunctionState:G8RMathTeacher
 *
 * Date Created: Feb/26/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

import G8R.serialization.CookieList;
import G8R.serialization.G8RRequest;
import G8R.serialization.G8RResponse;

public enum G8RMathTeacher implements G8RFunction {
    MATH("Math") {
        @Override
        public G8RResponse next(G8RRequest req) {
            return new G8RResponse();
        }
    }, ADD("add") {
        @Override
        public G8RResponse next(G8RRequest req) {
            return new G8RResponse();
        }
    }, SUBTRACT("subtract") {
        @Override
        public G8RResponse next(G8RRequest req) {
            return new G8RResponse();
        }
    }, MULTIPLY("multiply") {
        @Override
        public G8RResponse next(G8RRequest req) {
            return new G8RResponse();
        }
    };

    private String name;

    G8RMathTeacher(String str) {
        name = str;
    }

    public String getName() {
        return name;
    }
}
