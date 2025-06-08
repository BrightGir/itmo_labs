package ru.bright.server;


import ru.bright.*;

import java.io.*;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public class ServerReadingHandler {

    private Server server;

    public ServerReadingHandler(Server server) {
        this.server = server;
    }

    protected Callable<UserRequest> handleReading(SelectionKey key) {
        Callable<UserRequest> doRead = () -> {

            //ServerMain.getLogger().log(Level.INFO, "hamdlereading = ");

            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(100000);
            int bytesRead = 0;
            try {
                bytesRead = clientChannel.read(buffer);
            } catch (ClosedChannelException e) {
                ServerMain.getLogger().log(Level.SEVERE, "Error while deserializing object from client", e);
                //  try {
                    //      clientChannel.close();
                    //  } catch (IOException ex) {
                    //      ServerMain.getLogger().log(Level.FINE, "Ошибка при закрытии канала", ex);
                    //  }
                if(key != null && key.isValid()) {
                    key.cancel();
                }
                return null;
            }
            if (bytesRead == -1) {
                try {
                    clientChannel.close();
                } catch (IOException ex) {
                    ServerMain.getLogger().log(Level.FINE, "Ошибка при закрытии канала", ex);
                }
                if(key != null && key.isValid()) {
                    key.cancel();
                }
                return null;
            }
            buffer.flip();
            byte[] data = new byte[bytesRead];
            buffer.get(data);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object object = ois.readObject();
                if (!(object instanceof UserRequest)) {
                    server.responseToClient(null, key, new Response(ResponseStatus.ERROR, "Wrong request format"));
                    return null;
                }
                UserRequest request = (UserRequest) object;

             //   System.out.println("UserRequest = " + request.getCommandLine() + ",\n" +
             //           request.getAttachedObject().toString());

                return request;
            } catch (EOFException e){
              // try {
              //     clientChannel.close();
              // } catch (IOException ex) {
              //     ServerMain.getLogger().log(Level.FINE, "Ошибка при закрытии канала", ex);
              // }
              // if(key != null && key.isValid()) {
              //     key.cancel();
              // }
                e.printStackTrace();
                return null;
            } catch (IOException | ClassNotFoundException e) {
                ServerMain.getLogger().log(Level.SEVERE, "Error while deserializing object from client", e);
                server.responseToClient(null, key, new Response(ResponseStatus.ERROR, e.getMessage()));
            }
            return null;
        };
        return doRead;
    }
}
