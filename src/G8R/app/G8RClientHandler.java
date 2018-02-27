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

            G8RRequest res = (G8RRequest)G8RMessage.decode(in);
            Enum e = G8RFunctionFactory.getByName(res.getFunction());

            if(e != null) {
                while (e != ((G8RFunction)e).last()) {
                    e = handleRequest(res, e, out);

                    if(e != ((G8RFunction)e).last()) {
                        res = (G8RRequest) G8RMessage.decode(in);
                    }
                }
            } else {
                logger.log(Level.WARNING, errFunction, res);
                new G8RResponse(G8RResponse.type_ERROR, "NULL", errFunction,
                        res.getCookieList()).encode(out);
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
     * @param function function state from the client
     * @param out output sink
     */
    private Enum<?> handleRequest
    (G8RMessage clientMess, Enum function, MessageOutput out)
            throws ValidationException, IOException {
        logger.log(Level.INFO, msgRecivMessage, clientMess);
        G8RRequest clientRequest = (G8RRequest) clientMess;

        if(!clientRequest.getFunction().equals(
                ((G8RFunction) function).getName())) {
            new G8RResponse(G8RResponse.type_ERROR, "NULL", errFunction,
                    clientMess.getCookieList()).encode(out);
            return ((G8RFunction) function).last();
        } else {
            return ((G8RFunction) function).next(clientRequest, out);
        }
    }
}
