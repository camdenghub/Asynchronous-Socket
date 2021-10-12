import me.camden.client.AsynchronousClient;
import me.camden.client.listener.SocketListener;

/**
 * @author Camden on 10/7/2021
 **/
public class TestAsynchronousClient {

    public static void main(String[] args) {
        // Deploy Test
        final AsynchronousClient asynchronousClient = new AsynchronousClient("localhost", 32907);

        asynchronousClient.setSocketListener(new SocketListener() {
            @Override
            public void onConnection(AsynchronousClient asynchronousClient) {
                System.out.println("Connected to server.");
            }

            @Override
            public void onDisconnection(AsynchronousClient asynchronousClient) {
                System.out.println("Disconnected from server.");
            }

            @Override
            public void onPacket(AsynchronousClient asynchronousClient, String packetString) {
                System.out.println(packetString);
            }
        });

        final Thread thread = asynchronousClient.start();
    }
}
