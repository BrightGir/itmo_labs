package ru.bright.server;


import ru.bright.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public class ServerReadingHandler {

    private Server server;

    public ServerReadingHandler(Server server) {
        this.server = server;
    }

    protected Callable<UserRequest> handleReading(SelectionKey key) throws IOException {
        Callable<UserRequest> doRead = () -> {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(100000);
            int bytesRead = 0;
            try {
                bytesRead = clientChannel.read(buffer);
            } catch (EOFException e){

            } catch (IOException e) {
                ServerMain.getLogger().log(Level.SEVERE, "Error while deserializing object from client", e);
                server.responseToClient(key, new Response(ResponseStatus.ERROR, e.getMessage()));
                return null;
            }
            if (bytesRead == -1) {
                key.cancel();
                //Сохранение
                return null;
            }
            buffer.flip();
            byte[] data = new byte[bytesRead];
            buffer.get(data);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object object = ois.readObject();
                if (!(object instanceof UserRequest)) {
                    server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong request format"));
                    return null;
                }
                UserRequest request = (UserRequest) object;
                return request;
            } catch (EOFException e){

            } catch (IOException | ClassNotFoundException e) {
                ServerMain.getLogger().log(Level.SEVERE, "Error while deserializing object from client", e);
                server.responseToClient(key, new Response(ResponseStatus.ERROR, e.getMessage()));
            }
            return null;
        };
        return doRead;
    }
}
