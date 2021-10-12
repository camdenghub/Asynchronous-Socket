package me.camden.server.listener;

import me.camden.server.AsynchronousServer;
import me.camden.server.threads.SocketClientConnection;

/**
 * @author Camden on 10/7/2021
 **/
public interface SocketListener {

    void onConnection(AsynchronousServer asynchronousServer, SocketClientConnection socketClientConnection);

    void onDisconnection(AsynchronousServer asynchronousServer, SocketClientConnection socketClientConnection);

    void onPacket(AsynchronousServer asynchronousServer, SocketClientConnection socketClientConnection, String packetString);
}