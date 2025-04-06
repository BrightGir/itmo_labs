package ru.bright.server;


import ru.bright.*;
import ru.bright.commands.Command;
import ru.bright.commands.CommandType;
import ru.bright.commands.types.ServerObjectableCommand;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

public class ServerReadingHandler {

    private Server server;

    public ServerReadingHandler(Server server) {
        this.server = server;
    }

    protected void handleReading(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);
        if(bytesRead == -1) {
            key.cancel();
            //Сохранение
            return;
        }
        buffer.flip();
        byte[] data = new byte[bytesRead];
        buffer.get(data);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object object = ois.readObject();
            if(!(object instanceof Request)) {
                server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong request format"));
            }
            Request request = (Request) object;
            if(request.getAttachedObject() == null) {
                server.getServerCommandManager().executeCommand(key,request.getCommandLine());
            } else {
                String cmdName = request.getCommandLine().split(" ")[0];
                CommandType type;
                try {
                    type = CommandType.valueOf(cmdName.toUpperCase());
                } catch (Exception e) {
                    server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong command"));
                    return;
                }
                if(!server.getServerCommandManager().getCommandMap().containsKey(type)) {
                    server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong command"));
                    return;
                }
                Command cmd = server.getServerCommandManager().getCommandMap().get(type);
                if(cmd instanceof ServerObjectableCommand) {
                    server.getServerCommandManager().executeObjectableCommand(key,request.getCommandLine(),request.getAttachedObject());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            Main.getLogger().log(Level.SEVERE, "Error while deserializing object from client", e);
            server.responseToClient(key, new Response(ResponseStatus.ERROR, e.getMessage()));
        }

    }
}
