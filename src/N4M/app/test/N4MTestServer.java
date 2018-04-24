/*
 * N4M.app.test:N4MTestServer
 * Created on 4/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package N4M.app.test;

import N4M.serialization.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class N4MTestServer {

    public static void main(String[] args) throws IOException, N4MException {
        if(args.length != 1) {
            throw new IllegalArgumentException("Usage <port>");
        }

        int serverPort = Integer.parseInt(args[0]);

        DatagramSocket soc = new DatagramSocket(serverPort);

        int buffsize = soc.getReceiveBufferSize()-20;

        while(true) {
            DatagramPacket pack = new DatagramPacket(new byte[buffsize], buffsize);

            soc.receive(pack);
            N4MQuery message = (N4MQuery) N4MMessage.decode
                    (Arrays.copyOf(pack.getData(), pack.getLength()));

            pack.setData(new N4MResponse
                    (ErrorCodeType.NE, message.getMsgId() + 1, 0,
                            new ArrayList<>()).encode());
            soc.send(pack);
        }
    }
}
