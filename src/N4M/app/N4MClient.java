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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class N4MClient {

    public static String errCommandParams = "Usage: <server ip/name> " +
            "<server port> <Business name>";

    private static int messageId = (int) (Math.random() * Integer.MAX_VALUE);

    public static void main(String[] args) throws UnknownHostException {
        if(args.length != 3) {
            throw new IllegalArgumentException(errCommandParams);
        }

        InetAddress serverName = Inet4Address.getByName(args[0]);
        Integer serverPort = Integer.parseInt(args[1]);
        String businessName = args[2];
        DatagramPacket packet;

        N4MQuery clientAsks;
        N4MMessage message;
        try(DatagramSocket soc = new DatagramSocket(serverPort, serverName)) {
            //create client message
            clientAsks = new N4MQuery(messageId, businessName);

            //send to server
            byte[] encodedMsg = clientAsks.encode();
            packet = new DatagramPacket(encodedMsg, 0, encodedMsg.length);
            soc.send(packet);

            //receive from server
            soc.receive(packet);

            //decode message from server
            message = N4MMessage.decode(packet.getData());

            //print server response
            printResponse(message);
        } catch(N4MException n4me) {
            System.err.println(n4me);
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
