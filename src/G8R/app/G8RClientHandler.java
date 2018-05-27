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
import N4M.serialization.ApplicationEntry;
import N4M.serialization.N4MException;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.*;

public class G8RClientHandler implements Runnable {

    //error message to client
    private static final String errFunction = "Unexpected function";

    //messages to client
    private static final String msgG8R = "G8R ";
    private static final String msgConnection = "connected to: ";
    private static final String msgCloseConnect = "closed connection to: ";
    private static final String msgClientClose = " ***client terminated";

    private static final String LOGGERNAME = G8RServer.class.getName();

    //class variables
    private ArrayList<ApplicationEntry> appList;
    private Socket client;
    private Logger logger = Logger.getLogger(LOGGERNAME);
    private MessageInput in;
    private MessageOutput out;

    /**
     * creates a new clientHandler runnable
     * @param socket the client connection
     */
    public G8RClientHandler(Socket socket, ArrayList<ApplicationEntry> list)
            throws SocketException {
        appList = list;
        client = Objects.requireNonNull(socket);
        client.setSoTimeout(20000);
    }

    @Override
    public void run() {
        try {
            logger.info(msgG8R + msgConnection +
                    client.getLocalSocketAddress());

            in = new MessageInput(client.getInputStream());
            out = new MessageOutput(client.getOutputStream());

            G8RMessage mess = G8RMessage.decode(in);
            Enum e = G8RFunctionFactory.getByName(mess.getFunction());

            if(e != null) {
                incrementApp(e);
                while((e = handleRequest(mess, e, out)) !=
                        ((G8RFunction)e).last()) {
                    logger.log(Level.INFO, buildConnection(mess, false), mess);

                    mess = G8RMessage.decode(in);
                }
            } else {
                mess = new G8RResponse(G8RResponse.type_ERROR, "NULL",
                        errFunction, mess.getCookieList());
                mess.encode(out);
                logger.log(Level.WARNING, buildConnection(mess, true), mess);
            }

            client.close();
            logger.info(msgG8R + msgCloseConnect +
                    client.getLocalSocketAddress());
        } catch(ValidationException ve) {
            try {
                if(!out.isNull()) {
                    new G8RResponse(G8RResponse.type_ERROR, "NULL",
                            ve.getReason(), new CookieList()).encode(out);
                }
            } catch (ValidationException | IOException ignored) {
            }
            logger.warning(msgG8R + ve.getReason() + msgClientClose);
        } catch(IOException e) {
            logger.severe(msgG8R + e.getMessage() + msgClientClose);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void incrementApp(Enum funct) {
        for (ApplicationEntry ae : appList) {
            if(ae.getApplicationName().equals(((G8RFunction)funct).getName())) {
                try {
                    ae.setAccessCount(ae.getAccessCount() + 1);
                } catch (N4MException ignored) {}
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
        logger.log(Level.INFO, buildConnection(clientMess, true), clientMess);
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

    /**
     * builds the logging message for a connection
     * @param message the message sent or received
     * @param sentOrReceive which message to build (sent or received)
     * @return string representation of connection message
     */
    private String buildConnection(G8RMessage message, boolean sentOrReceive) {
        if(sentOrReceive) {
            return msgG8R + client.getLocalSocketAddress() + "-" +
                    Thread.currentThread() + " [Received:" + message + "]";
        } else {
            return msgG8R + client.getLocalSocketAddress() + "-" +
                    Thread.currentThread() + " [Sent:" + message + "]";
        }
    }
}
