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

import G8R.app.FunctionState.G8RFunctionFactory;
import N4M.app.N4MClientHandler;
import N4M.serialization.ApplicationEntry;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class G8RServerAIO {

    //private static final String LOGGERCONFIG = "./logs/.properties";
    private static final String LOGNAME = G8RServer.class.getName();
    private static final String LOGDIR = "logs";
    private static final String G8RLOGFILE = LOGDIR + "/onnections.log";
    private static final String N4MLOGFILE = LOGDIR + "/n4m.log";

    private static final String errParams =
            "Usage: <server port>";
    private static final String errServerCrash = "Server crashed";
    private static final String errThread = "could not close thread";
    private static final String errServerSetup = "error setting up server";
    private static final String errLogger = "could not create logger";
    private static final String errGroup = "could not create async group";

    private static final String msgServerStart = "server started on port: ";
    private static final String msgServerEnd = " server closed";
    private static final String msgG8R = "G8R ";
    private static final String msgN4M = "N4M ";

    private static Logger logger = null;
    private static ArrayList<ApplicationEntry> appList = new ArrayList<>();
    private static ArrayList<Thread> threadList = new ArrayList<>();
    private static int servPort;
    private static AsynchronousChannelGroup group;
    private static Attachment a;

    /**
     * sends and receives messages from multiple clients
     * @param argv arguments to passed to server
     */
    public static void main(String[] argv) {
        if(argv.length != 1) {
            throw new IllegalArgumentException(errParams);
        }

        servPort = Integer.parseInt(argv[0]);

        //initializes logger
        try {
            setup_logger();
        } catch(IOException ioe) {
            logger.severe(errLogger);
            System.exit(-1);
        }

        setup_applicationList();

        //client handlers
        handle_N4M();
        handle_G8R();

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

        //creates a log files
        new File(LOGDIR).mkdirs();
        new File(G8RLOGFILE).createNewFile();
        new File(N4MLOGFILE).createNewFile();

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
     */
    private static void setup_applicationList() {
        appList.addAll(G8RFunctionFactory.values());
    }

    private static void setup_sever(AsynchronousServerSocketChannel serv) {
        try {
            serv.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serv.bind(new InetSocketAddress(servPort));
        } catch(IOException ioe) {
            logger.severe(errServerSetup);
        }
    }

    /**
     * handles a G8R request
     */
    private static void handle_G8R() {
        logger.info(msgG8R + msgServerStart + servPort);

        try {
            try {
                group = AsynchronousChannelGroup.withThreadPool
                        (Executors.newSingleThreadExecutor());
                try (AsynchronousServerSocketChannel server =
                             AsynchronousServerSocketChannel.open(group)) {

                    //server setup
                    setup_sever(server);

                    a = new Attachment(0L, server, appList);
                    server.accept(a, new G8RClientHandlerAIO());

                    group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                } catch (IOException e) {
                    System.err.println(msgG8R + errServerCrash);
                    logger.severe(msgG8R + errServerCrash);
                } catch (InterruptedException ignored) {
                }
            } catch (IOException e) {
                logger.severe(errGroup);
                group.shutdown();
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE, msgG8R + errServerCrash, e);
        }
    }

    /**
     * handles a N4M request
     */
    private static void handle_N4M() {
        Thread t = new Thread(() -> {
            try(DatagramSocket servUDP = new DatagramSocket(servPort)) {
                logger.info(msgN4M + msgServerStart + servUDP.getLocalPort());
                int maxPacketSize = servUDP.getReceiveBufferSize()-20;

                while(true) {
                    try {
                        DatagramPacket p = new DatagramPacket
                                (new byte[maxPacketSize], maxPacketSize);

                        servUDP.receive(p);
                        N4MClientHandler handler =
                                new N4MClientHandler(p, appList, a.lassAccess);

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

    public static class Attachment {
        long lassAccess;
        AsynchronousServerSocketChannel server;
        ArrayList<ApplicationEntry> appEntries;

        public Attachment(long aTime, AsynchronousServerSocketChannel aServ,
                          ArrayList<ApplicationEntry> apps) {
            lassAccess = aTime;
            server = aServ;
            appEntries = apps;
        }
    }
}