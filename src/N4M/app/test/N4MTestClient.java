/*
 * N4M.app.test:N4MtestClient
 *
 * Date Created: Apr/11/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.app.test;

import N4M.serialization.N4MException;
import N4M.serialization.N4MMessage;
import N4M.serialization.N4MResponse;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class N4MTestClient {

    private static final String errCommandLine =
            "Usage: <server name> <server port>";

    public static void main(String[] args) throws UnknownHostException {
        if(args.length != 2) {
            throw new IllegalArgumentException(errCommandLine);
        }

        InetAddress servName = Inet4Address.getByName(args[0]);
        int servPort = Integer.parseInt(args[1]);

        try(DatagramSocket soc = new DatagramSocket()) {
            int maxPacketSize = soc.getReceiveBufferSize()-20;

            byte[] test = new byte[]{0x32,0x01,0x00};
            DatagramPacket pack =
                    new DatagramPacket(test, test.length, servName, servPort);

            soc.send(pack);

            pack = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);

            soc.receive(pack);
            printPack(pack);
        } catch (IOException | N4MException e) {
            e.printStackTrace();
        }
    }

    private static void printPack(DatagramPacket pack) throws N4MException {
        N4MResponse res = (N4MResponse)N4MMessage.decode
                (Arrays.copyOf(pack.getData(), pack.getLength()));
        System.out.println(res);
    }
}
