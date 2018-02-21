/*
 * G8R.app:G8RClientHandler
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import java.net.Socket;
import java.util.Objects;

public class G8RClientHandler implements Runnable {

    Socket client = null;

    public G8RClientHandler(Socket socket) {
        client = Objects.requireNonNull(socket);
    }

    @Override
    public void run() {

    }
}
