package ru.bright.server;

import ru.bright.ServerMain;
import ru.bright.Response;
import ru.bright.UserRequest;

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

    protected void responseToClient(UserRequest request, SelectionKey key, Response response) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        response.setUuid(request.getUuid());
        System.out.println("send something to client uuid:, response: " + response.getUuid());
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(response);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                int bytesWrittenThisTurn = clientChannel.write(buf);
                if (bytesWrittenThisTurn == 0) {
                    // Канал не может принять данные прямо сейчас (буфер полон).
                    // В полноценном NIO здесь нужно регистрировать OP_WRITE и ждать.
                    // Для ПРОСТОГО ИСПРАВЛЕНИЯ БЕСКОНЕЧНОГО ЦИКЛА, если не хотите OP_WRITE:
                    System.err.println("RESPONDING_HANDLER: Write buffer full. Aborting send for this response.");
                    // Можно просто прервать отправку для этого ответа или закрыть соединение.
                    // Чтобы точно остановить цикл и нагрев, выбросим исключение.
                    throw new IOException("Socket send buffer full, cannot write more data at this moment.");
                }
            }
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error while response to client", e);
         //  try {
         //      if (clientChannel != null && clientChannel.isOpen()) {
         //          clientChannel.close();
         //      }
         //  } catch (IOException ex) {
         //      ServerMain.getLogger().log(Level.FINE, "Error closing channel" );
         //  }
            if(key != null && key.isValid()) {
                key.cancel();
            }
        } finally {

        }
    }

    protected void responseToClient(UserRequest request, SelectionKey key, SocketChannel clientChannel, Response response) {
        try {
            response.setUuid(request.getUuid());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(response);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                int bytesWrittenThisTurn = clientChannel.write(buf);
                if (bytesWrittenThisTurn == 0) {
                    // Канал не может принять данные прямо сейчас (буфер полон).
                    // В полноценном NIO здесь нужно регистрировать OP_WRITE и ждать.
                    // Для ПРОСТОГО ИСПРАВЛЕНИЯ БЕСКОНЕЧНОГО ЦИКЛА, если не хотите OP_WRITE:
                    System.err.println("RESPONDING_HANDLER: Write buffer full. Aborting send for this response.");
                    // Можно просто прервать отправку для этого ответа или закрыть соединение.
                    // Чтобы точно остановить цикл и нагрев, выбросим исключение.
                    throw new IOException("Socket send buffer full, cannot write more data at this moment.");
                }
            }
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error while response to client", e);
          //  if(clientChannel != null) {
          //      try {
          //          clientChannel.close();
          //      } catch (IOException ex) {
          //          ex.printStackTrace();
          //      }
          //  }
            if(key != null) {
                key.cancel();
            }
        } finally {

        }
    }
}
