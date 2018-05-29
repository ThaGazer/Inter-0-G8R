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
        public G8RCalculator next(G8RMessage req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Math(req, out);
        }

        @Override
        public G8RCalculator nextFunct() {
            return FUNCT;
        }
    },FUNCT("Operator") {
        @Override
        public G8RCalculator next(G8RMessage req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Funct(req, out);
        }

        @Override
        public G8RCalculator nextFunct() {
            return ADD;
        }
    }, ADD("add") {
        @Override
        public G8RCalculator next(G8RMessage req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Add(req, out);
        }

        @Override
        public G8RCalculator nextFunct() {
            return SUBTRACT;
        }
    }, SUBTRACT("subtract") {
        @Override
        public G8RCalculator next(G8RMessage req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Subtract(req, out);
        }

        @Override
        public G8RCalculator nextFunct() {
            return MULTIPLY;
        }
    }, MULTIPLY("multiply") {
        @Override
        public G8RCalculator next(G8RMessage req, MessageOutput out)
                throws IOException, ValidationException {
            return state_Multiply(req, out);
        }

        @Override
        public G8RCalculator nextFunct() {
            return EXIT;
        }
    }, EXIT("fin") {
        @Override
        public G8RCalculator next(G8RMessage req, MessageOutput out)
                throws ValidationException, IOException {
            return state_Exit(req, out);
        }

        @Override
        public G8RCalculator nextFunct() {
            return NULL;
        }
    }, NULL("NULL") {
        @Override
        public G8RCalculator next(G8RMessage request, MessageOutput out) {
            return NULL;
        }

        @Override
        public G8RCalculator nextFunct() {
            return null;
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

    /**
     * enum contructor
     * @param str name of enum
     */
    G8RCalculator(String str) {
        name = str;
    }

    /**
     * the first enum in the function
     * @return the first enum
     */
    public static G8RCalculator first() {
        return MATH;
    }

    /**
     * the last enum in the function set
     * @return the last enum
     */
    public G8RCalculator last() {
        return NULL;
    }

    /**
     * name of enum
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * handles the math state
     * @param mess G8R message
     * @param out output sink
     * @return next state
     * @throws ValidationException if G8R error
     * @throws IOException if I/O problem
     */
    protected G8RCalculator state_Math(G8RMessage mess, MessageOutput out)
            throws ValidationException, IOException {
        mess.getCookieList().add(cookie_Sum, "0");

        mess = buildOkResponse(FUNCT.getName(), msgWhich + msgFunction,
                mess.getCookieList());
        mess.encode(out);
        return FUNCT;
    }

    /**
     * handles function state
     * @param mess G8R message
     * @param out output sink
     * @return next state
     * @throws ValidationException if G8R error
     * @throws IOException if I/O problem
     */
    protected G8RCalculator state_Funct(G8RMessage mess, MessageOutput out)
            throws ValidationException, IOException {
        G8RRequest req = (G8RRequest)mess;

        if(req.getParams().length != 1) {
            mess = buildErrResponse(FUNCT.getName(),
                    errFunct + msgWhich + msgFunction, req.getCookieList());
            mess.encode(out);
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
            mess = buildErrResponse(FUNCT.getName(), message,
                    mess.getCookieList());
            mess.encode(out);
            return FUNCT;
        }

        mess = buildOkResponse(state.getName(), message,
                req.getCookieList());
        mess.encode(out);
        return state;
    }

    /**
     * handles add state
     * @param mess G8R message
     * @param out output sink
     * @return next state
     * @throws ValidationException if G8R error
     * @throws IOException if I/O problem
     */
    protected G8RCalculator state_Add(G8RMessage mess, MessageOutput out)
            throws ValidationException, IOException {
        G8RRequest req = (G8RRequest)mess;

        if(req.getParams().length < 1) {
            mess = buildErrResponse(ADD.getName(), errAdd + msgEnterNum + msgAdd,
                    req.getCookieList());
            mess.encode(out);
            return ADD;
        }

        for(String s : req.getParams()) {
            try {
                int result = Integer.parseInt(req.getCookieList().
                        getValue(cookie_Sum)) + Integer.parseInt(s);
                req.getCookieList().add(cookie_Sum, String.valueOf(result));
            } catch(NumberFormatException nfe) {
                mess = buildErrResponse(ADD.getName(),
                        errNumberFormat + msgEnterNum + msgAdd,
                        req.getCookieList());
                mess.encode(out);
                return ADD;
            }
        }

        mess = buildOkResponse(FUNCT.getName(), buildSum(req.getCookieList()),
                req.getCookieList());
        mess.encode(out);
        return FUNCT;
    }

    /**
     * handles subtraction state
     * @param mess G8R message
     * @param out output sink
     * @return next state
     * @throws ValidationException if G8R error
     * @throws IOException if I/O problem
     */
    protected G8RCalculator state_Subtract(G8RMessage mess, MessageOutput out)
            throws ValidationException, IOException {
        G8RRequest req = (G8RRequest)mess;

        if(req.getParams().length < 1) {
            mess = buildErrResponse(SUBTRACT.getName(), errSub + msgEnterNum + msgSub,
                    req.getCookieList());
            mess.encode(out);
            return SUBTRACT;
        }

        for(String s : req.getParams()) {
            try {
                int result = Integer.parseInt(req.getCookieList().
                        getValue(cookie_Sum)) - Integer.parseInt(s);
                req.getCookieList().add(cookie_Sum, String.valueOf(result));
            } catch(NumberFormatException nfe) {
                mess = buildErrResponse(SUBTRACT.getName(),
                        errNumberFormat + msgEnterNum + msgSub,
                        req.getCookieList());
                mess.encode(out);
                return SUBTRACT;
            }
        }

        mess = buildOkResponse(FUNCT.getName(), buildSum(req.getCookieList()),
                req.getCookieList());
        mess.encode(out);
        return FUNCT;
    }

    /**
     * handles multiply state
     * @param mess G8R message
     * @param out output sink
     * @return next state
     * @throws ValidationException if G8R error
     * @throws IOException if I/O problem
     */
    protected G8RCalculator state_Multiply(G8RMessage mess, MessageOutput out)
            throws ValidationException, IOException {
        G8RRequest req = (G8RRequest)mess;

        if(req.getParams().length < 1) {
            mess = buildErrResponse(MULTIPLY.getName(), errMult + msgEnterNum +
                            msgMult, req.getCookieList());
            mess.encode(out);
            return MULTIPLY;
        }

        for(String s : req.getParams()) {
            try {
                int result = Integer.parseInt(req.getCookieList().
                        getValue(cookie_Sum)) * Integer.parseInt(s);
                req.getCookieList().add(cookie_Sum, String.valueOf(result));
            } catch(NumberFormatException nfe) {
                mess = buildErrResponse(MULTIPLY.getName(),
                        errNumberFormat + msgEnterNum + msgMult,
                        req.getCookieList());
                mess.encode(out);
                return MULTIPLY;
            }
        }

        mess = buildOkResponse(FUNCT.getName(), buildSum(req.getCookieList()),
                req.getCookieList());
        mess.encode(out);
        return FUNCT;
    }

    /**
     * handles the exit state
     * @param mess G8R message
     * @param out ouput sink
     * @return the next state
     * @throws ValidationException if could not build message
     * @throws IOException if I/O problem
     */
    protected G8RCalculator state_Exit(G8RMessage mess, MessageOutput out)
            throws ValidationException, IOException {
        mess = buildOkResponse(NULL.getName(), buildFinalSum(mess.getCookieList()),
                mess.getCookieList());
        mess.encode(out);
        return NULL;
    }

    /**
     * builds a sum message
     * @param cookies the cookies contains the sum
     * @return the built message
     */
    private String buildSum(CookieList cookies) {
        return msgSum + cookies.getValue(cookie_Sum) + msgFunction;
    }

    /**
     * builds the final summation message
     * @param cookies the cookies containing the sum
     * @return the built message
     */
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
