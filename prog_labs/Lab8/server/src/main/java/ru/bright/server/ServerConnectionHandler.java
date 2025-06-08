package ru.bright.server;

import ru.bright.ServerMain;
import ru.bright.Response;
import ru.bright.ResponseStatus;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

public class ServerConnectionHandler {

    private Server server;

    public ServerConnectionHandler(Server server) {
        this.server = server;
    }

    protected void handleNewConnection(SelectionKey key, ServerSocketChannel serverChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        if(clientChannel != null) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            Response response = new Response(ResponseStatus.OK, "Connection accepted");
            server.responseToClient(null,key, clientChannel, response);
            ServerMain.getLogger().log(Level.INFO,"Connection accepted");
        }
    }
}
