package ru.bright;

import ru.bright.gui.MainApp;
import ru.bright.util.BasicConsole;
import ru.bright.util.ClientCommandManager;
import ru.bright.util.Console;
import ru.bright.utils.AppAction;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private ServerConnectionManager serverConnectionManager;
    private Map<UUID, CompletableFuture<Response>> sendedRequests;

    public Client(Console console, int portNumber) {
        this.portNumber = portNumber;
        this.console = console;
        this.clientChannel = null;
        this.isAvailable = false;
        this.serverConnectionManager = new ServerConnectionManager(this);
        this.sendedRequests = new HashMap<>();
    }


   // public boolean openConnection() throws IOException {
   //     selector = null;
   //     try {
   //         clientChannel = SocketChannel.open();
   //         clientChannel.configureBlocking(false);
   //         clientChannel.connect(new InetSocketAddress(portNumber));
   //         selector = Selector.open();
   //         clientChannel.register(selector, SelectionKey.OP_CONNECT);
   //         isAvailable = true;
   //         return true;
   //     } catch (Exception e) {
   //         console.printErr(e.getMessage());
   //       //  if(selector != null && selector.isOpen()) {
   //       //      selector.close();
   //       //  }
   //         setNotAvailable();
   //         return false;
   //     }
   // }

    /*
    private void closeCurrentConnectionResources() {
        if (selector != null && selector.isOpen()) {
            try {
                selector.close();
                // console.println("Selector closed.");
            } catch (IOException e) {
                console.printErr("Error closing selector: " + e.getMessage());
            }
            selector = null;
        }
        if (clientChannel != null && clientChannel.isOpen()) {
            try {
                clientChannel.close();
                // console.println("Client channel closed.");
            } catch (IOException e) {
                console.printErr("Error closing client channel: " + e.getMessage());
            }
            clientChannel = null;
        }
        isAvailable = false; // Убеждаемся, что флаг сброшен
    }
*/

    public boolean openConnection() { // Убрал throws IOException, т.к. обрабатываем внутри
      //  closeCurrentConnectionResources(); // Сначала закрываем всё старое
        selector = null; // Убедимся, что селектор будет новым
        clientChannel = null; // И канал

        try {
            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false); // Важно для селектора
            // console.println("Attempting to connect...");
            clientChannel.connect(new InetSocketAddress(portNumber));

            selector = Selector.open();
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
            isAvailable = true; // Предварительно ставим true, finishConnect подтвердит
            // console.println("Connection process initiated. isAvailable: " + isAvailable);
            return true; // Инициация подключения началась
        } catch (Exception e) {
            console.printErr("Error opening connection: " + e.getMessage());
            // e.printStackTrace(); // Для отладки
            setNotAvailable(); // Явно вызовем, чтобы очистить sendedRequests, хотя closeCurrentConnectionResources это уже сделает
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
            } catch (ConnectException e) {
                e.printStackTrace();
                console.printErr("Сервер временно недоступен. Попробуйте позже");
               // selector.close();
                setNotAvailable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    public CompletableFuture<Response> requestToServer(UserRequest request) {
        CompletableFuture<Response> future = new CompletableFuture<>();

        // 1. Проверяем, есть ли активное соединение
        if (clientChannel == null || !clientChannel.isOpen() || !isAvailable) {
            String message = "Client.requestToServer: No active connection to server. Cannot send request UUID: " + request.getUuid();
            console.printErr(message);
            future.completeExceptionally(new IOException(message)); // Завершаем Future ошибкой
            return future; // Возвращаем Future, который уже завершен ошибкой
        }

        // 2. Если соединение есть, пытаемся отправить
        sendedRequests.put(request.getUuid(), future);
        try {
            // console.println("CLIENT: Отправка запроса UUID: " + request.getUuid());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(request);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
            oos.flush(); // Важно добавить flush после записи объекта!
            // console.println("CLIENT: Запрос UUID " + request.getUuid() + " отправлен (" + data.length + " байт).");

        } catch (IOException e) { // Если ошибка при ЗАПИСИ (например, канал закрылся, пока мы писали)
            String errorMsg = "Client.requestToServer: IOException while writing request UUID: " + request.getUuid() + ". Error: " + e.getMessage();
            console.printErr(errorMsg);
            // e.printStackTrace(); // Для отладки
            try {
                openConnection();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            // Соединение стало плохим. Помечаем как недоступное и закрываем ресурсы.
            setNotAvailable();
            future.completeExceptionally(new IOException(errorMsg, e)); // Завершаем Future ошибкой
        }
        return future; // Возвращаем Future, который будет завершен, когда придет ответ или если была ошибка
    }
    */



    private ResponseStatus readFromServer(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ResponseStatus status = null;
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        try {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                setNotAvailable();
                key.cancel();
                return null;
            }
        } catch (Exception e) {
            setNotAvailable();
            key.cancel();
        }

        System.out.println("try read");
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {

            while(bais.available() > 0) {
                try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Object object = ois.readObject();
                    Response response = (Response) object;

                    System.out.println("get from server, response UUID  " + response.getUuid());
                    if(sendedRequests.containsKey(response.getUuid())) {
                        sendedRequests.get(response.getUuid()).complete(response);
                        System.out.println("get response UUID=" + response.getUuid());
                        sendedRequests.remove(response.getUuid());
                    }


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
                    } else if(status == ResponseStatus.UPDATE) {
                        System.out.println("Notify to update");
                        MainApp.notify(AppAction.UPDATE);
                    }
                } catch (EOFException e) {
                    e.printStackTrace();
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

    public void setNotAvailable(){
        isAvailable = false;
        sendedRequests.clear();
    }


    public CompletableFuture<Response> requestToServer(UserRequest request) throws IOException{
        CompletableFuture<Response> future = new CompletableFuture<>();
        request.setUuid(UUID.randomUUID());
        System.out.println("send request uuid=" + request.getUuid());
        sendedRequests.put(request.getUuid(),future);
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(request);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        } catch (IOException e) {
            console.printErr("Error while writing request: " + e.getMessage());
      //      e.printStackTrace();
            try {
                openConnection();
            } catch (Exception e1) {
                //e1.printStackTrace();
                e1.printStackTrace();
              //  future.completeExceptionally(e1);
                return null;
            }
            return null;
        }
        return future;
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

    public boolean isAvailable() {
        return isAvailable;
    }

}
