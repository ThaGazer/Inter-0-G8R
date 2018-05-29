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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class G8RClientHandlerAIO
        implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    private static final String errFunction = "Unexpected function";
    private static final String errClose = "could not close connection";
    private static final String errRead = "failed to read from client";
    private static final String errWrite = "failed to write to client";
    private static final String errConnection = "failed to accept a connection";
    private static final String errMaxCount =
            "reached the max count for an application";

    private static final String msgG8R = "G8R ";
    private static final String msgConnection = "connected to: ";
    private static final String msgCloseConnect = "closed connection to: ";
    private static final String msgClientClose = " ***client terminated";

    private static final String LOGGERNAME = G8RServer.class.getName();

    private static final int CLIENTTIMEOUT = 20000;
    private static final int BUFFERMAXSIZE = 4096;

    private Logger logger = Logger.getLogger(LOGGERNAME);
    private List<ApplicationEntry> appEntries = new ArrayList<>();
    private AsynchronousSocketChannel client;
    private G8RMessage message;

    @Override
    public void completed
            (AsynchronousSocketChannel channel, Attachment attach) {
        if(attach.server.isOpen()) {

            client = channel;
            try {
                logger.info(msgG8R + msgConnection + client.getRemoteAddress());
            } catch (IOException e) {
                logger.severe(msgG8R + errConnection);
            }

            appEntries.clear();
            appEntries.addAll(attach.appEntries);

            //update lass access
            attach.lassAccess = TimeUnit.MILLISECONDS.toSeconds(
                    new Date().getTime());

            //accept next connection
            attach.server.accept(attach, this);

            /*make this a ByteBuffer[] to allow for
              larger inputs and a more structured read formats
            */
            //input sink
            ByteBuffer buffArr = ByteBuffer.allocate(BUFFERMAXSIZE);

            readFrom(buffArr, new readHandler());
        }
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
                } catch (N4MException ignored) {
                    logger.warning(errMaxCount);
                }
            }
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

    /**
     * reads input from a client
     * @param buff buffer to read into
     */
    private void readFrom(ByteBuffer buff, readHandler reader) {
        client.read(buff, CLIENTTIMEOUT, TimeUnit.MILLISECONDS,
                buff, reader);
    }

    private void writeTo(byte[] buffer, writeHandler writer) {
        ByteBuffer buff = ByteBuffer.wrap(buffer);
        client.write(buff, buff, writer);
    }

    private class readHandler implements
            CompletionHandler<Integer, ByteBuffer> {

        @Override
        public void completed(Integer result, ByteBuffer buff) {
            if(client.isOpen()) {
                if (result == -1) {
                    try {
                        client.close();
                        logger.warning(msgG8R + errRead + msgClientClose);
                    } catch (IOException e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                    }
                } else {
                    try {
                        message = G8RMessage.decode(new MessageInput(
                                new ByteArrayInputStream(buff.array())));
                        try {
                            logger.log(Level.INFO,
                                    buildConnection(message, true));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream bOut =
                                new ByteArrayOutputStream();
                        MessageOutput out = new MessageOutput(bOut);

                        Enum e = G8RFunctionFactory.getByName
                                (message.getFunction());

                        if(e != null) {
                            incrementApp(e);
                            e = ((G8RFunction)e).next(message, out);

                            if(e == ((G8RFunction) e).last()) {
                                buff = ByteBuffer.wrap(bOut.toByteArray());
                                writeTo(buff.array(), new writeHandler());

                                logger.info(msgG8R + msgCloseConnect +
                                        client.getRemoteAddress());
                                client.close();
                            } else {
                                buff = ByteBuffer.wrap(bOut.toByteArray());
                                writeTo(buff.array(), new writeHandler());

                                buff = ByteBuffer.allocate(BUFFERMAXSIZE);
                                readFrom(buff, new readHandler());
                            }
                        } else {
                            new G8RResponse(G8RResponse.type_ERROR, "NULL",
                                    errFunction, message.getCookieList()).encode
                                    (out);
                            logger.log(Level.WARNING,
                                    buildConnection(message, true), message);
                            writeTo(bOut.toByteArray(), new writeHandler());
                        }
                    } catch (IOException | ValidationException e) {
                        readFrom(buff, this);
                    }
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            logger.log(Level.WARNING, errRead, exc);
            try {
                client.close();
            } catch (IOException e) {
                logger.warning(msgG8R + errClose);
            }
        }
    }

    private class writeHandler implements
            CompletionHandler<Integer, ByteBuffer> {

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            if(client.isOpen()) {
                if (result == -1) {
                    try {
                        client.close();
                        logger.warning(msgG8R + errWrite + msgClientClose);
                    } catch(IOException e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                    }
                } else if(attachment.hasRemaining()) {
                    client.write(attachment, attachment, this);
                } else {
                    try {
                        message = G8RMessage.decode(new MessageInput(new ByteArrayInputStream(attachment.array())));
                        logger.info(msgG8R + buildConnection(message, false));
                    } catch (ValidationException | IOException e) {
                        e.printStackTrace();
                    }
                    attachment.clear();
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            logger.log(Level.WARNING, errWrite, exc);
            if(client.isOpen()) {
                try {
                    client.close();
                } catch (IOException e) {
                    logger.warning(msgG8R + errClose);
                }
            }
        }
    }
}
