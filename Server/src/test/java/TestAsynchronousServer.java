import me.camden.server.AsynchronousServer;
import me.camden.server.threads.SocketClientConnection;
import me.camden.server.listener.SocketListener;

/**
 * @author Camden on 10/7/2021
 **/
public class TestAsynchronousServer {

    public static void main(String[] args) {
        // Deploy Test
        try {
            final AsynchronousServer asynchronousServer = new AsynchronousServer(32907);
            asynchronousServer.setSocketListener(new SocketListener() {
                @Override
                public void onConnection(AsynchronousServer asynchronousServer, SocketClientConnection socketClientConnection) {
                    System.out.println("New Client Connection.");
                }

                @Override
                public void onDisconnection(AsynchronousServer asynchronousServer, SocketClientConnection socketClientConnection) {
                    System.out.println("A Client Has Disconnected.");
                }

                @Override
                public void onPacket(AsynchronousServer server, SocketClientConnection socketClientConnection, String packetString) {
                    System.out.println(packetString);
                }
            });

            final Thread thread = asynchronousServer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
