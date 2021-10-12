package me.camden.server;

import me.camden.server.threads.SocketClientConnection;
import me.camden.server.listener.SocketListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Camden on 10/7/2021
 **/
public class AsynchronousServer implements Runnable {

    private final List<SocketClientConnection> connections = new LinkedList<>();

    private final AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    private SocketListener socketListener;

    public AsynchronousServer(int port) throws Exception {
        this.asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
        asynchronousServerSocketChannel.bind(new InetSocketAddress("localhost", port));
    }

    public Thread start() {
        final Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting Socket Server.");
            while (getAsynchronousServerSocketChannel().isOpen()) {
                final Future<AsynchronousSocketChannel> acceptConnection = getAsynchronousServerSocketChannel().accept();
                final AsynchronousSocketChannel client = acceptConnection.get();
                if (client != null && client.isOpen()) {
                    new Thread(new SocketClientConnection(this, client)).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getAsynchronousServerSocketChannel().isOpen()) {
                try {
                    getAsynchronousServerSocketChannel().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Socket Server Shutdown.");
            }
        }
    }

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public SocketListener getSocketListener() {
        return socketListener;
    }

    public List<SocketClientConnection> getConnections() {
        return connections;
    }

    public AsynchronousServerSocketChannel getAsynchronousServerSocketChannel() {
        return asynchronousServerSocketChannel;
    }
}
