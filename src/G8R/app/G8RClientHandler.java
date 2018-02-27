/*
 * G8R.app:G8RClientHandler
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.app.FunctionState.G8RFunction;
import G8R.app.FunctionState.G8RFunctionFactory;
import G8R.serialization.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class G8RClientHandler implements Runnable {

    //error message to client
    private static final String errFunction = "Unexpected function";

    //messages to client
    private static final String msgConnection = "connected to: ";
    private static final String msgCloseConnect = "closed connection to: ";
    private static final String msgSendMessage = "sending to client";
    private static final String msgRecivMessage = "receive from client";

    //class variables
    private Socket client;
    private Logger logger = Logger.getLogger(G8RServer.class.getName());

    /**
     * creates a new clientHandler runnable
     * @param socket the client connection
     */
    public G8RClientHandler(Socket socket) {
        client = Objects.requireNonNull(socket);
    }

    @Override
    public void run() {
        try {
            logger.info(msgConnection + client.getLocalSocketAddress());

            MessageInput in = new MessageInput(client.getInputStream());
            MessageOutput out = new MessageOutput(client.getOutputStream());

            G8RResponse res = (G8RResponse)G8RMessage.decode(in);
            while(G8RFunctionFactory.getByName(res.getFunction()) != null) {
                res = handleRequest(G8RMessage.decode(in));

                res.encode(out);
                logger.log(Level.INFO, msgSendMessage, res);
            }

            client.close();
            logger.info(msgCloseConnect + client.getLocalSocketAddress());
        } catch(ValidationException ve) {
            logger.warning(ve.getReason());
        } catch(IOException e) {
            logger.severe(e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * handler of the state received from client
     * @param clientMess the message from the client
     * @return the response to the client
     * @throws ValidationException if message validation error
     */
    private G8RResponse handleRequest(G8RMessage clientMess)
            throws ValidationException {
        logger.log(Level.INFO, msgRecivMessage, clientMess);
        CookieList clientCookies = clientMess.getCookieList();
        G8RRequest clientRequest = (G8RRequest) clientMess;

        Enum e = G8RFunctionFactory.getByName(clientRequest.getFunction());
        if(e == null) {
            return new G8RResponse(
                    G8RResponse.type_ERROR, "NULL", errFunction, clientCookies);
        } else {
            return ((G8RFunction)e).next(clientRequest);
        }

    }
}
