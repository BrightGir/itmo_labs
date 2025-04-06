package ru.bright;

import ru.bright.util.BasicConsole;
import ru.bright.util.ClientCommandManager;
import ru.bright.util.Console;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

public class Client {

    private int portNumber;
    private Console console;
    private SocketChannel clientChannel;
    private ClientCommandManager commandManager;
    private Selector selector;
    private BufferedReader consoleReader;
    private boolean isAvailable;
    private String lastCommand;

    public Client(Console console, int portNumber) {
        this.portNumber = portNumber;
        this.console = console;
        this.clientChannel = null;
        this.isAvailable = false;
    }

    public boolean openConnection() throws IOException {
        selector = null;
        try {
            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);
            clientChannel.connect(new InetSocketAddress(portNumber));

            selector = Selector.open();
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
            isAvailable = true;
            return true;
        } catch (Exception e) {
            console.printErr(e.getMessage());
            if(selector != null && selector.isOpen()) {
                selector.close();
            }
            isAvailable = false;
            return false;
        }
    }

    public void startListening() throws IOException {
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
        ((BasicConsole)console).setReader(consoleReader);
        while (true) {
            if(consoleReader.ready()) {
                String line = consoleReader.readLine();
                if (line == null) break;
                if (line.isEmpty()) {
                    continue;
                }
                if(line.equals("exit")) {
                    consoleReader.close();
                    System.exit(0);
                }
                lastCommand = line;
                if(!isAvailable) {
                    openConnection();
                    continue;
                }
                commandManager.executeCommand(line);
            }
            if(!isAvailable) {
                continue;
            }
            try {
                selector.selectNow();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isConnectable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }
                        console.println("Connected to " + channel.getRemoteAddress());
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        if(lastCommand != null && !lastCommand.isEmpty()) {
                            commandManager.executeCommand(lastCommand);
                        }
                    }
                    if (selectionKey.isReadable()) {
                        readFromServer(selectionKey);
                    }
                    iterator.remove();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                console.printErr("Сервер временно недоступен. Попробуйте позже");
                selector.close();
                isAvailable = false;
            }
        }
    }



    private void readFromServer(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int bytesRead = clientChannel.read(buffer);
        if(bytesRead == -1) {
            isAvailable = false;
            key.cancel();
            return;
        }
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {

            while(bais.available() > 0) {
                try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Object object = ois.readObject();
                    Response response = (Response) object;
                    if (response.getStatus() == ResponseStatus.OK) {
                        console.println(response.getMessage());
                    } else if (response.getStatus() == ResponseStatus.ERROR) {
                        console.printErr("Server error:" + response.getMessage());
                    } else if (response.getStatus() == ResponseStatus.DENIED) {
                        console.printErr("Command denied:" + response.getMessage());
                    }
                } catch (EOFException e) {
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    console.printErr("Error while reading response: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            console.printErr("Error while reading response: " + e.getMessage());
        }
        buffer.clear();

    }

    public void requestToServer(Request request) throws IOException{
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(request);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        } catch (Exception e) {
            console.printErr("Error while writing request: ");
          //  e.printStackTrace();
        }

    }

    public Console getConsole() {
        return console;
    }

    public void setCommandManager(ClientCommandManager clientCommandManager) {
        this.commandManager = clientCommandManager;
    }
}
