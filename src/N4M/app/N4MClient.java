/*
 * N4M.app:N4MClient
 *
 * Date Created: Mar/26/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.app;

import N4M.serialization.*;
import java.io.IOException;
import java.net.*;

public class N4MClient {

    private static String errCommandParams = "Usage: <server ip/name> " +
            "<server port> <Business name>";
    private static String errHost = "could not connect to: ";
    private static String errInit = "could not initialize ";
    private static String causeSocket = "socket connection";


    private static int messageId = (int) (Math.random() * Integer.MAX_VALUE);
    private static DatagramSocket soc;

    public static void main(String[] args) {
        if(args.length != 3) {
            throw new IllegalArgumentException(errCommandParams);
        }

        InetAddress serverName = null;
        try {
            serverName = Inet4Address.getByName(args[0]);
        } catch(UnknownHostException uhe) {
            System.err.println(errHost + args[0]);
            System.exit(1);
        }

        //reads port
        Integer serverPort = Integer.parseInt(args[1]);

        //reads business name
        String businessName = args[2];

        N4MQuery clientAsks;
        try(DatagramSocket soc = new DatagramSocket()) {
            DatagramPacket packet;

            //create client message
            clientAsks = new N4MQuery(messageId, businessName);

            //send to server
            byte[] encodedMsg = clientAsks.encode();
            packet = new DatagramPacket(encodedMsg, encodedMsg.length,
                    serverName, serverPort);
            soc.send(packet);

            //receive from server
            soc.receive(packet);

            //decode message from server
            N4MMessage message = N4MMessage.decode(packet.getData());

            //print server response
            printResponse(message);
        } catch(N4MException n4me) {
            System.err.println(n4me.getMessage());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void printResponse(N4MMessage message) {
        N4MResponse res = (N4MResponse) message;

        System.out.println(res.getTimeStamp());
        System.out.println(res.getMsgId());
        System.out.println(res.getErrorCodeNum());

        for(ApplicationEntry ae : res.getApplications()) {
            System.out.println(ae);
        }
    }
}