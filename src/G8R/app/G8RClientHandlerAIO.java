/*
 * G8R.app:G8RClientHandlerAIO
 *
 * Date Created: Apr/24/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.app.FunctionState.G8RFunction;
import G8R.app.FunctionState.G8RFunctionFactory;
import G8R.serialization.*;
import G8R.app.G8RServerAIO.Attachment;
import N4M.serialization.ApplicationEntry;
import N4M.serialization.N4MException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class G8RClientHandlerAIO
        implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    private static final String errFunction = "Unexpected function";
    private static final String errClose = "could not close connection";
    private static final String errRead = "failed to read from client";
    private static final String errConnection = "failed to accept a connection";

    private static final String msgG8R = "G8R ";
    private static final String msgConnection = "connected to: ";
    private static final String msgCloseConnect = "closed connection to: ";
    private static final String msgClientClose = " ***client terminated";

    private static final String LOGGERNAME = G8RServer.class.getName();

    private static final int CLIENTTIMEOUT = 20000;

    private Logger logger = Logger.getLogger(LOGGERNAME);
    private AsynchronousSocketChannel client;
    private ArrayList<ApplicationEntry> appEntries;
    private G8RMessage message;

    @Override
    public void completed
            (AsynchronousSocketChannel channel, Attachment attach) {
        if(attach.server.isOpen()) {
            attach.server.accept(null, this);
        }
        client = channel;

        attach.lassAccess = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime());

        //input sink
        ByteBuffer[] buffArr = new ByteBuffer[]{ByteBuffer.allocate(4096)};
        client.read(buffArr, 0, 1, CLIENTTIMEOUT, TimeUnit.MILLISECONDS, buffArr, new readHandler());

/*        //input sink
        ByteArrayInputStream bIn = new ByteArrayInputStream(buffer.array());
        MessageInput in = new MessageInput(bIn);

        //output sink
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        try {
            G8RMessage mess = G8RMessage.decode(in);
            Enum e = G8RFunctionFactory.getByName(mess.getFunction());

            if (e != null) {
                incrementApp(e);
                while ((e = handleRequest(mess, e, out)) !=
                        ((G8RFunction) e).last()) {
                    logger.log(Level.INFO, buildConnection(mess, false), mess);

                    client.read(buffer, CLIENTTIMEOUT, TimeUnit.MILLISECONDS, buffer, new readHandler());
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
                    client.getRemoteAddress());
        } catch (IOException | ValidationException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void failed(Throwable exc, Attachment attach) {
        logger.log(Level.WARNING, errConnection, exc);
    }

    /**
     * increments the access count for a function
     * @param funct function to increment use count
     */
    private void incrementApp(Enum funct) {
        for (ApplicationEntry ae : appEntries) {
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
    private String buildConnection(Object message, boolean sentOrReceive)
            throws IOException {
        if(sentOrReceive) {
            return msgG8R + client.getRemoteAddress() + "-" +
                    Thread.currentThread() + " [Received:" + message + "]";
        } else {
            return msgG8R + client.getRemoteAddress() + "-" +
                    Thread.currentThread() + " [Sent:" + message + "]";
        }
    }

    private class readHandler implements
            CompletionHandler<Long, ByteBuffer[]> {

        @Override
        public void completed(Long result, ByteBuffer[] buff) {
            if(client.isOpen()) {
                if (result == -1) {
                    try {
                        client.close();
                        logger.warning(msgG8R + errRead);
                    } catch (IOException e) {
                        logger.warning(msgG8R + errClose);
                    }
                    System.exit(-1);
                }

                try {
                    message = G8RMessage.decode(new MessageInput(new ByteArrayInputStream(buff[0].array())));
                    try {
                        logger.log(Level.INFO, buildConnection(message, true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException | ValidationException e) {
                    client.read(buff, 0, 1, CLIENTTIMEOUT, TimeUnit.MILLISECONDS, buff, this);
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer[] attachment) {
            logger.log(Level.WARNING, errRead, exc);
        }
    }
}
