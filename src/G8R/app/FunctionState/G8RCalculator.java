/*
 * G8R.app.FunctionState:G8RCalculator
 *
 * Date Created: Feb/26/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;


import G8R.serialization.*;

import java.io.IOException;

public enum G8RCalculator implements G8RFunction {
    MATH("Math") {
        @Override
        public G8RCalculator next(G8RRequest req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Math(req, out);
        }
    },FUNCT("Operator") {
        @Override
        public G8RCalculator next(G8RRequest req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Funct(req, out);
        }
    }, ADD("add") {
        @Override
        public G8RCalculator next(G8RRequest req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Add(req, out);
        }
    }, SUBTRACT("subtract") {
        @Override
        public G8RCalculator next(G8RRequest req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Subtract(req, out);
        }
    }, MULTIPLY("multiply") {
        @Override
        public G8RCalculator next(G8RRequest req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Multiply(req, out);
        }
    }, EXIT("fin") {
        @Override
        public G8RCalculator next(G8RRequest request, MessageOutput out)
                throws ValidationException, IOException {
            return state_Exit(request, out);
        }
    }, NULL("NULL") {
        @Override
        public G8RCalculator next(G8RRequest request, MessageOutput out) {
            return NULL;
        }
    };

    private static final String errFunct = "Poorly formed math function. ";
    private static final String errAdd = "Poorly formed add function. ";
    private static final String errSub = "Poorly formed subtract function. ";
    private static final String errMult = "Poorly formed multiply function. ";
    private static final String errNumberFormat = "Not a number ";
    private static final String errUnexpectedFunction = "Unexpected function ";

    private static final String msgWhich =
            "Which math function would you like to run?";
    private static final String msgFunction = " Function> ";
    private static final String msgEnterNum = "Enter numbers to ";
    private static final String msgAdd = "add: ";
    private static final String msgSub = "subtract: ";
    private static final String msgMult = "multiply: ";
    private static final String msgSum = "The sum is: ";
    private static final String msgFinalSum = "The final sum is: ";

    private static final String cookie_Sum = "Sum";

    private String name;

    G8RCalculator(String str) {
        name = str;
    }

    public G8RCalculator first() {
        return MATH;
    }

    public G8RCalculator last() {
        return NULL;
    }

    public String getName() {
        return name;
    }

    protected G8RCalculator state_Math(G8RRequest req, MessageOutput out)
            throws ValidationException, IOException {
        req.getCookieList().add(cookie_Sum, "0");

        buildOkResponse(FUNCT.getName(), msgWhich + msgFunction,
                req.getCookieList()).encode(out);
        return FUNCT;
    }

    protected G8RCalculator state_Funct(G8RRequest req, MessageOutput out)
            throws ValidationException, IOException {
        if(req.getParams().length != 1) {
            buildErrResponse(FUNCT.getName(), errFunct + msgWhich + msgFunction,
                    req.getCookieList()).encode(out);
            return FUNCT;
        }

        String param = req.getParams()[0];
        String message = msgEnterNum;
        G8RCalculator state;
        if(param.equals(ADD.getName())) {
            message += msgAdd;
            state = ADD;
        } else if(param.equals(SUBTRACT.getName())) {
            message += msgSub;
            state = SUBTRACT;
        } else if(param.equals(MULTIPLY.getName())) {
            message += msgMult;
            state = MULTIPLY;
        } else if(param.equals(EXIT.getName())) {
            return state_Exit(req, out);
        } else {
            message = errUnexpectedFunction + msgWhich + msgFunction;
            buildErrResponse(FUNCT.getName(), message,
                    req.getCookieList()).encode(out);
            return FUNCT;
        }

        buildOkResponse(state.getName(), message,
                req.getCookieList()).encode(out);
        return state;
    }

    protected G8RCalculator state_Add(G8RRequest req, MessageOutput out)
            throws ValidationException, IOException {
        if(req.getParams().length < 1) {
            buildErrResponse(ADD.getName(), errAdd + msgEnterNum + msgAdd,
                    req.getCookieList()).encode(out);
            return ADD;
        }

        for(String s : req.getParams()) {
            try {
                int result = Integer.parseInt(req.getCookieList().
                        getValue(cookie_Sum)) + Integer.parseInt(s);
                req.getCookieList().add(cookie_Sum, String.valueOf(result));
            } catch(NumberFormatException nfe) {
                buildErrResponse(ADD.getName(),
                        errNumberFormat + msgEnterNum + msgAdd,
                        req.getCookieList()).encode(out);
                return ADD;
            }
        }

        buildOkResponse(FUNCT.getName(), buildSum(req.getCookieList()),
                req.getCookieList()).encode(out);
        return FUNCT;
    }

    protected G8RCalculator state_Subtract(G8RRequest req, MessageOutput out)
            throws ValidationException, IOException {
        if(req.getParams().length < 1) {
            buildErrResponse(SUBTRACT.getName(), errSub + msgEnterNum + msgSub,
                    req.getCookieList()).encode(out);
            return SUBTRACT;
        }

        for(String s : req.getParams()) {
            try {
                int result = Integer.parseInt(req.getCookieList().
                        getValue(cookie_Sum)) - Integer.parseInt(s);
                req.getCookieList().add(cookie_Sum, String.valueOf(result));
            } catch(NumberFormatException nfe) {
                buildErrResponse(SUBTRACT.getName(),
                        errNumberFormat + msgEnterNum + msgSub,
                        req.getCookieList()).encode(out);
                return SUBTRACT;
            }
        }

        buildOkResponse(FUNCT.getName(), buildSum(req.getCookieList()),
                req.getCookieList()).encode(out);
        return FUNCT;
    }

    protected G8RCalculator state_Multiply(G8RRequest req, MessageOutput out)
            throws ValidationException, IOException {
        if(req.getParams().length < 1) {
            buildErrResponse(MULTIPLY.getName(), errMult + msgEnterNum +
                            msgMult, req.getCookieList()).encode(out);
            return MULTIPLY;
        }

        for(String s : req.getParams()) {
            try {
                int result = Integer.parseInt(req.getCookieList().
                        getValue(cookie_Sum)) * Integer.parseInt(s);
                req.getCookieList().add(cookie_Sum, String.valueOf(result));
            } catch(NumberFormatException nfe) {
                buildErrResponse(MULTIPLY.getName(),
                        errNumberFormat + msgEnterNum + msgMult,
                        req.getCookieList()).encode(out);
                return MULTIPLY;
            }
        }

        buildOkResponse(FUNCT.getName(), buildSum(req.getCookieList()),
                req.getCookieList()).encode(out);
        return FUNCT;
    }

    protected G8RCalculator state_Exit(G8RRequest req, MessageOutput out)
            throws ValidationException, IOException {
        buildOkResponse(NULL.getName(), buildFinalSum(req.getCookieList()),
                req.getCookieList()).encode(out);
        return NULL;
    }

    private String buildSum(CookieList cookies) {
        return msgSum + cookies.getValue(cookie_Sum) + msgFunction;
    }

    private String buildFinalSum(CookieList cookies) {
        return msgFinalSum + cookies.getValue(cookie_Sum);
    }

    /**
     * builds a ok response message using the status and message passed in
     * @param state state of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildOkResponse
    (String state, String message, CookieList cookies)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_OK, state, message,
                cookies);
    }

    /**
     * builds an error response message using the status and message passed in
     * @param state state of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildErrResponse
    (String state, String message, CookieList cookies)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_ERROR, state,
                message, cookies);
    }
}
