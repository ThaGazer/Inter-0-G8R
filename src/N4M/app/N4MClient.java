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
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class N4MClient {

    private static final String errCommandParams = "Usage: <server ip/name> " +
            "<server port> <Business name>";
    private static final String errHost = "could not connect to: ";
    private static final String errMsgId = "unexpected message Id";

    private static final String fieldTime = "Timestamp: ";
    private static final String fieldId = "Message Id: ";
    private static final String fieldErrorCode = "Error code: ";
    private static final String fieldApplications = "Applications: ";

    private static int messageId = (int) (Math.random() * 255);

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
            soc.setSoTimeout(20000);
            DatagramPacket packet;
            int maxPacketSize = soc.getReceiveBufferSize()-20;

            //create client message
            clientAsks = new N4MQuery(messageId, businessName);

            //send to server
            byte[] encodedMsg = clientAsks.encode();
            packet = new DatagramPacket(encodedMsg, encodedMsg.length,
                    serverName, serverPort);
            soc.send(packet);

            //receive from server
            N4MMessage message;
            packet = new DatagramPacket(new byte[maxPacketSize],
                    maxPacketSize);
            soc.receive(packet);

            //decode message from server
            message = N4MMessage.decode(Arrays.copyOf(packet.getData(), packet.getLength()));

            //print server response
            printResponse(message);
        } catch(N4MException n4me) {
            n4me.printReason();
        } catch(IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    /**
     * prints a response message from the server if it matches the clients Id
     * @param message packet from server
     */
    private static void printResponse(N4MMessage message) {
        if (messageId == message.getMsgId()) {
            N4MResponse res = (N4MResponse) message;
            long timestamp = TimeUnit.SECONDS.toMillis(res.getTimestamp());

            System.out.println(fieldId + res.getMsgId());

            if(timestamp == 0) {
                System.out.println(fieldTime + timestamp);
            } else {
                System.out.println(fieldTime + new Date(timestamp));
            }
            System.out.println(fieldErrorCode + res.getErrorCode());
            System.out.println(fieldApplications);
            for (ApplicationEntry ae : res.getApplications()) {
                System.out.println(ae);
            }
        }
        else {
            System.out.println(errMsgId);
        }
    }
}
