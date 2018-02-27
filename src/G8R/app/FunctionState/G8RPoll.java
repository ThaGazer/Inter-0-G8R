/*
 * G8R.app:G8RPollState
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

import G8R.serialization.CookieList;
import G8R.serialization.G8RRequest;
import G8R.serialization.G8RResponse;
import G8R.serialization.ValidationException;

import java.util.Set;

public enum G8RPoll implements G8RFunction {
    POLL("Poll") {
        @Override
        public G8RResponse next(G8RRequest request) throws ValidationException {
            return state_Poll(request);
        }
    }, NAMESTEP("NameStep") {
        @Override
        public G8RResponse next(G8RRequest request) throws ValidationException {
            return state_NameStep(request);
        }
    }, FOODMOOD("FoodMood") {
        @Override
        public G8RResponse next(G8RRequest request) throws ValidationException {
            return state_FoodMood(request);
        }
    };

    private static final String errName = "Poorly formed name. ";
    private static final String errMood = "Poorly formed food mood. ";

    private static final String msgNameStep = "Name (First Last)> ";
    private static final String msgFoodMood = "'s food mood> ";
    private static final String msgBaseDiscount = "10% + ";
    private static final String msgStoreDiscount = "% off at McDonalds";

    private static final String cookie_fName = "FName";
    private static final String cookie_lName = "LName";
    private static final String cookie_repeat = "Repeat";

    private String name;


    G8RPoll(String str) {
        name = str;
    }

    public String getName() {
        return name;
    }

    /**
     * handles the poll state
     * @param request a request message
     * @return a response message to client
     * @throws ValidationException if response validation error
     */
    protected G8RResponse state_Poll(G8RRequest request)
            throws ValidationException {
        G8RPoll state;
        String message;

        Set<String> cookieNames = request.getCookieList().getNames();
        if(cookieNames.contains(cookie_fName) &&
                cookieNames.contains(cookie_fName)) {
            state = NAMESTEP;
            message = msgNameStep;
        } else {
            state = FOODMOOD;
            message = buildFoodMood(request.getCookieList());
        }

        return buildOkResponse(state.getName(), message,
                request.getCookieList());
    }

    /**
     * handles the name step state
     * @param request a request message
     * @return a response message to client
     * @throws ValidationException if response validation error
     */
    protected G8RResponse state_NameStep(G8RRequest request)
            throws ValidationException {
        if(request.getParams().length != 2) {
            return buildErrResponse(NAMESTEP.getName(), errName + msgNameStep,
                    request.getCookieList());
        }
        request.getCookieList().add(cookie_fName, request.getParams()[0]);
        request.getCookieList().add(cookie_lName, request.getParams()[1]);

        return buildOkResponse
                (FOODMOOD.getName(), buildFoodMood(request.getCookieList()),
                        request.getCookieList());
    }

    /**
     * handles the food mood state
     * @param request a request message
     * @return a response message to client
     * @throws ValidationException if response validation error
     */
    protected G8RResponse state_FoodMood(G8RRequest request)
            throws ValidationException {
        if(request.getParams().length != 1) {
            return buildErrResponse(FOODMOOD.getName(),
                    errMood + buildFoodMood(request.getCookieList()),
                    request.getCookieList());
        }

        if(request.getCookieList().getValue(cookie_repeat) == null) {
            request.getCookieList().add(cookie_repeat, "0");
        }

        request.getCookieList().add(cookie_repeat, addToCookie(cookie_repeat,
                request.getCookieList()));

        return buildOkResponse("NULL", buildDiscount(request.getCookieList()),
                request.getCookieList());
    }

    private String buildFoodMood(CookieList cookies) {
        return cookies.getValue(cookie_fName) + msgFoodMood;
    }

    private String buildDiscount(CookieList cookies) {
        return msgBaseDiscount +
                cookies.getValue(cookie_repeat) + msgStoreDiscount;
    }

    /**
     * builds a ok response message using the status and message passed in
     * @param status status of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildOkResponse
    (String status, String message, CookieList cookies)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_OK, status, message,
                cookies);
    }

    /**
     * builds an error response message using the status and message passed in
     * @param status status of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildErrResponse
    (String status, String message, CookieList cookies)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_ERROR, status,
                message, cookies);
    }

    /**
     * adds one to the repeat cookie
     * @return string representation of an int
     */
    private String addToCookie(String name, CookieList cookies) {
        return String.valueOf(
                Integer.parseInt(cookies.getValue(name)) + 1);
    }

}
