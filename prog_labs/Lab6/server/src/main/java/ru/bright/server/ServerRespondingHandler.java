package ru.bright.server;

import ru.bright.Main;
import ru.bright.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

public class ServerRespondingHandler {

    private Server server;

    public ServerRespondingHandler(Server server) {
        this.server = server;
    }

    protected void responseToClient(SelectionKey key, Response response) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(response);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        } catch (IOException e) {
            Main.getLogger().log(Level.SEVERE, "Error while response to client", e);
            if(key != null) {
                key.cancel();
            }
            if(!(clientChannel != null && !clientChannel.isOpen())) {
                server.shutdown();
            }
        } finally {

        }
    }
}
