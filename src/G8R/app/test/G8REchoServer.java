/*
 * G8R.app:G8REchoServer
 *
 * Date Created: Feb/19/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class G8REchoServer {

    private static final String usage = "Usage: <server port>";
    private static final String serverStatus = "Server running on: ";

    public static void main(String[] argv) {
        if(argv.length != 1) {
            throw new IllegalArgumentException(usage);
        }
        int servPort = Integer.parseInt(argv[0]);

        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket servSock = new ServerSocket(servPort)) {
            System.out.println(serverStatus + servSock.getLocalSocketAddress());

            while(true) {
                try {
                    pool.execute(new G8RTestHandler(servSock.accept()));
                } catch(Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }


}
