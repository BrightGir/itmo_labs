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
    private boolean isAuthorized;
    private User user;

    public Client(Console console, int portNumber) {
        this.portNumber = portNumber;
        this.console = console;
        this.clientChannel = null;
        this.isAvailable = false;
      //  this.user = new User("","");
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
                if(isAuthorized || (line.trim().equalsIgnoreCase("auth") || line.trim().equalsIgnoreCase("register"))) {
                    if(isAuthorized && (line.trim().equalsIgnoreCase("auth") || line.trim().equalsIgnoreCase("register"))) {
                        console.println("Вы уже авторизованы");
                    } else {
                        commandManager.executeCommand(user, line);
                    }
                } else {
                    console.printErr("Access error. Enter to system or register. (Commands: auth/register)");
                }
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
                        console.println("You can enter to system or register (Commands: auth/register)");
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        if(isAuthorized && lastCommand != null && !lastCommand.isEmpty()) {
                            commandManager.executeCommand(user, lastCommand);
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



    private ResponseStatus readFromServer(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ResponseStatus status = null;
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int bytesRead = clientChannel.read(buffer);
        if(bytesRead == -1) {
            isAvailable = false;
            key.cancel();
            return null;
        }
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {

            while(bais.available() > 0) {
                try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Object object = ois.readObject();
                    Response response = (Response) object;
                    status = response.getStatus();
                    if (response.getStatus() == ResponseStatus.OK) {
                        console.println(response.getMessage());
                    } else if (response.getStatus() == ResponseStatus.ERROR) {
                        console.printErr("Server error:" + response.getMessage());
                    } else if (response.getStatus() == ResponseStatus.DENIED) {
                        console.printErr("Command denied:" + response.getMessage());
                    } else if(status == ResponseStatus.AUTH_PASSED) {
                        if(response.getMessage() != null) {
                            console.println(response.getMessage());
                        }
                        isAuthorized = true;
                    } else if(status == ResponseStatus.AUTH_FAILED) {
                        console.printErr("Access error. Enter to system or register. (Commands: auth/register)");
                        isAuthorized = false;
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
        return status;
    }

    public void requestToServer(UserRequest request) throws IOException{
      //  if(user == null) {
      //      System.out.println("user null");
      //  } else {
      //      System.out.println("user = " + user.getLogin() + " pass = " + user.getPassword());
      //  }
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(request);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        } catch (Exception e) {
            console.printErr("Error while writing request: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Console getConsole() {
        return console;
    }

    public void setCommandManager(ClientCommandManager clientCommandManager) {
        this.commandManager = clientCommandManager;
    }
}
