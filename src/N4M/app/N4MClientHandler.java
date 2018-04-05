/*
 * N4M.app:N4MClientHandler
 *
 * Date Created: Apr/04/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.app;

import G8R.app.G8RServer;
import N4M.serialization.*;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class N4MClientHandler {

    private static final String yaGotMe = "You somehow caught me officer";

    private static final String msgN4M = "N4M ";

    private static final String LOGGERNAME = G8RServer.class.getName();

    private Logger logger = Logger.getLogger(LOGGERNAME);
    private long timestamp;
    private DatagramPacket pack;
    private ArrayList<ApplicationEntry> appList = new ArrayList<>();

    public N4MClientHandler(DatagramPacket packet,
                            ArrayList<ApplicationEntry> list, long lastAccess) {
        setPacket(packet);
        timestamp = lastAccess;
        appList.addAll(list);
    }

    /**
     * sets a datagram packet
     * @param packet packet to set
     */
    private void setPacket(DatagramPacket packet) {
        pack = Objects.requireNonNull(packet);
    }

    /**
     * operates off of message from client
     * @return byte[] of encoded message
     */
    public byte[] response() {
        N4MQuery message;
        try {
            try {
                message = (N4MQuery)N4MMessage.decode(pack.getData());
                logger.info(buildLogMsg(message, true));
            } catch (N4MException n4me) {
                logger.warning(msgN4M + n4me.getReason());

                N4MResponse res = new N4MResponse();
                res.setErrorCodeNum(n4me.getErrorCodeType().getErrorCodeNum());

                return sendResponse(res);
            }

            return sendResponse(new N4MResponse
                    (0, message.getMsgId(), timestamp, appList));
        } catch(N4MException n4me) {
            System.err.println(yaGotMe);
            return null;
        }
    }

    /**
     * sends byte[] to client
     * @param res message to client
     * @return byte[] of message
     */
    private byte[] sendResponse(N4MResponse res) {
        logger.info(buildLogMsg(res, false));
        return res.encode();
    }

    /**
     * builds a message based on if sending or receiving a message
     * @param message message to build on
     * @param sentOrReceive sending or receiving
     * @return built message
     */
    private String buildLogMsg(N4MMessage message, boolean sentOrReceive) {
        if(sentOrReceive) {
            return msgN4M + "[Receive:From=" +
                    pack.getAddress().getHostAddress() + message + "]";
        } else {
            return msgN4M + "[Sent:" + message + "]";
        }
    }
}
