/*
 * G8R.app:G8RSeverAIO
 * Created on 4/21/2018
 *
 * Author(s):
 * -Justin Ritter
 */
/*
 * G8R.app:G8RServer
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.app.FunctionState.G8RFunction;
import G8R.app.FunctionState.G8RFunctionFactory;
import G8R.serialization.*;
import N4M.app.N4MClientHandler;
import N4M.serialization.ApplicationEntry;
import N4M.serialization.N4MException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class G8RServerAIO {

    //private static final String LOGGERCONFIG = "./logs/.properties";
    private static final String LOGNAME = G8RServer.class.getName();
    private static final String G8RLOGFILE = "./logs/connections.log";
    private static final String N4MLOGFILE = "./logs/n4m.log";

    private static final String errParams =
            "Usage: <server port>";
    private static final String errServerCrash = "Server crashed";
    private static final String errThread = "could not close thread";

    private static final String msgServerStart = "server started on port: ";
    private static final String msgServerEnd = "server closed";
    private static final String msgG8R = "G8R ";
    private static final String msgN4M = "N4M ";

    private static Logger logger = null;
    private static ArrayList<ApplicationEntry> appList = new ArrayList<>();
    private static ArrayList<Thread> threadList = new ArrayList<>();
    private static int servPort;
    private static long lastAccess = 0;

    /**
     * sends and receives messages from multiple clients
     * @param argv arguments to passed to server
     * @throws IOException if I/O problem
     */
    public static void main(String[] argv) throws IOException, N4MException {
        if(argv.length != 1) {
            throw new IllegalArgumentException(errParams);
        }

        servPort = Integer.parseInt(argv[0]);

        //initializes logger
        setup_logger();
        setup_applicationList();

        //client handlers
        handle_G8R();
        handle_N4M();

        for(Thread t : threadList) {
            try {
                t.join();
            } catch(InterruptedException e) {
                logger.severe(errThread);
            }
        }
    }

    /**
     * sets up the logger for the server
     * @throws IOException if I/O problem
     */
    private static void setup_logger() throws IOException {
        LogManager manager = LogManager.getLogManager();
        manager.reset();

        /*future implementation maybe
        manager.readConfiguration(new FileInputStream(LOGGERCONFIG));*/

        //initializes the logger
        logger = Logger.getLogger(LOGNAME);

        //defines handles for the logger
        Handler fileHand1 = new FileHandler(G8RLOGFILE);
        Handler fileHand2 = new FileHandler(N4MLOGFILE);
        Handler consoleHand = new ConsoleHandler();

        //filters
        fileHand1.setFilter(record -> record.getMessage().contains(msgG8R));
        fileHand2.setFilter(record -> record.getMessage().contains(msgN4M));

        fileHand1.setLevel(Level.ALL);
        fileHand2.setLevel(Level.ALL);
        consoleHand.setLevel(Level.INFO);

        //sets the formatting style of the logs
        consoleHand.setFormatter(new SimpleFormatter());

        logger.addHandler(fileHand1);
        logger.addHandler(fileHand2);
        logger.addHandler(consoleHand);
    }

    /**
     * creates an application list of all applications
     * @throws N4MException is could not get application
     */
    private static void setup_applicationList() throws N4MException {
        appList.addAll(G8RFunctionFactory.values());
    }

    /**
     * handles a G8R request
     */
    private static void handle_G8R() {
        logger.info(msgG8R + msgServerStart + servPort);
        try (AsynchronousServerSocketChannel server =
                     AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(servPort));

            while(true) {
                server.accept(new Attachments(server, appList), new G8RClientHandlerAIO());
                lastAccess = TimeUnit.MILLISECONDS.toSeconds
                        (new Date().getTime());
            }
        } catch (IOException e) {
            System.err.println(msgG8R + errServerCrash);
            logger.severe(msgG8R + errServerCrash);
        }
    }

    /**
     * handles a N4M request
     */
    private static void handle_N4M() {
        logger.info(msgN4M + msgServerStart + servPort);
        Thread t = new Thread(() -> {
            try(DatagramSocket servUDP = new DatagramSocket(servPort)) {
                int maxPacketSize = servUDP.getReceiveBufferSize()-20;

                while(true) {
                    try {
                        DatagramPacket p = new DatagramPacket
                                (new byte[maxPacketSize], maxPacketSize);

                        servUDP.receive(p);
                        N4MClientHandler handler =
                                new N4MClientHandler(p, appList, lastAccess);

                        p.setData(handler.response());
                        servUDP.send(p);
                    } catch(IOException ioe) {
                        logger.warning(msgN4M + ioe.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, msgN4M + errServerCrash, e);
            } finally {
                logger.info(msgN4M + msgServerEnd);
            }
        });
        threadList.add(t);
        t.start();
    }

    static class Attachments {
        AsynchronousServerSocketChannel server;
        ArrayList<ApplicationEntry> appList;

        Attachments(AsynchronousServerSocketChannel serv,
                    ArrayList<ApplicationEntry> apps) {
            server = serv;
            appList.addAll(apps);
        }
    }
}