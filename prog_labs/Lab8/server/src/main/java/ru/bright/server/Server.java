package ru.bright.server;


import ru.bright.*;
import ru.bright.managers.AuthManager;
import ru.bright.managers.CollectionManager;
import ru.bright.managers.DatabaseManger;
import ru.bright.managers.ServerCommandManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private int portNumber;
    private ServerConnectionHandler serverConnectionHandler;
    private ServerReadingHandler serverReadingHandler;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ServerCommandManager serverCommandManager;
    private ServerRespondingHandler serverRespondingHandler;
    private ServerProcessHandler serverProcessHandler;
    private Logger logger;
    private CollectionManager collectionManager;
    private DatabaseManger databaseManger;
    private AuthManager authManager;
    private ForkJoinPool requestReaderPool;
    private ForkJoinPool requestProcessPool;
    private ExecutorService requestResponserPool;

    public Server(int portNumber) {
        this.portNumber = portNumber;
        this.serverConnectionHandler = new ServerConnectionHandler(this);
        this.serverReadingHandler = new ServerReadingHandler(this);
        this.serverRespondingHandler = new ServerRespondingHandler(this);
        this.serverProcessHandler = new ServerProcessHandler(this);
        this.requestReaderPool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism());
        this.requestProcessPool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism());
        this.requestResponserPool = Executors.newCachedThreadPool();
    }

    public void startServer() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(portNumber));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            ServerMain.getLogger().log(Level.INFO, "Server listening on port: " + portNumber);
            while (true) {
                try {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            serverConnectionHandler.handleNewConnection(key, serverChannel, selector);
                        }
                        if (key.isReadable()) {
                            try {
                                if (key.isValid()) {
                                    initMultiThreadReading(key);
                                }
                            } catch (Exception e) {
                                ServerMain.getLogger().log(Level.SEVERE, "Error while client reading", e.getMessage());
                                key.cancel();
                            }
                        }
                    }
                } catch (CancelledKeyException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10);
            }
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error working server socket", e.getMessage());
        } catch (InterruptedException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error working thread", e.getMessage());
        }
        finally {
            try {
                if (serverChannel != null && serverChannel.isOpen()) {
                    serverChannel.close();
                }
                if (selector != null && selector.isOpen()) {
                    selector.close();
                }
            } catch (IOException e) {
                ServerMain.getLogger().log(Level.SEVERE, "Error closing server socket");
            }
        }
    }









/*
    protected void responseToClient(UserRequest request, SelectionKey key, Response response) {
        // ServerMain.getLogger().log(Level.INFO, "SERVER: responseToClient вызван для Request UUID: " + ...);
        Runnable doResponse = () -> {
            if (key == null || !key.isValid()) {
                // ServerMain.getLogger().log(Level.WARNING, "ResponseTask: Key is null or invalid.");
                return;
            }
            SocketChannel clientChannel = (SocketChannel) key.channel(); // Получаем канал из ключа
            if (clientChannel == null || !clientChannel.isOpen()) {
                // ServerMain.getLogger().log(Level.WARNING, "ResponseTask: Channel is null or closed for key " + key.hashCode());
                key.cancel(); // Отменяем ключ
                return;
            }
            // Передаем актуальный clientChannel, полученный из key
            serverRespondingHandler.responseToClient(request, key, clientChannel, response);
        };
        requestResponserPool.submit(doResponse);
    }

 */


    protected void responseToClient(UserRequest request, SelectionKey key, SocketChannel clientChannel, Response response) {
        Runnable doResponse = () -> {
            serverRespondingHandler.responseToClient(request, key,clientChannel, response);
        };
        requestResponserPool.submit(doResponse);
    }

    public void shutdown() {
        getServerCommandManager().saveFile();
        try {
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close();
            }
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            ServerMain.getLogger().log(Level.INFO, "Server is shutting down");
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error while shutting down server", e);
        } finally {
            requestReaderPool.shutdown();
            requestProcessPool.shutdown();
            requestResponserPool.shutdown();
            databaseManger.closeConnection();
            System.exit(1);
        }
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public void sendError(UserRequest request, SelectionKey key, String message) {
        responseToClient(request, key,new Response(ResponseStatus.ERROR,message));
    }

    public void sendOK(UserRequest request, SelectionKey key, String message) {
        responseToClient(request, key,new Response(ResponseStatus.OK,message));
    }

    public void sendObject(UserRequest request, SelectionKey key, Object object) {
        Response response = new Response(ResponseStatus.OK,null);
        response.setData(object);
        responseToClient(request, key,response);
    }

    public void sendAnother(UserRequest request, SelectionKey key, ResponseStatus status, String message) {
        if(status == ResponseStatus.UPDATE) {
            Set<SelectionKey> allKeys = new HashSet<>(selector.keys());
            for (SelectionKey k : allKeys) {
                if (k.isValid() && k.channel() instanceof SocketChannel) {
                    responseToClient(request, k,new Response(status,message));
                }
            }
            return;
        }
        responseToClient(request, key,new Response(status,message));
    }

    public DatabaseManger getDatabaseManger() {
        return databaseManger;
    }

    public void setDatabaseManager(DatabaseManger databaseManger) {
        this.databaseManger =databaseManger;
    }

    protected ServerConnectionHandler getServerConnectionHandler() {
        return serverConnectionHandler;
    }

    protected ServerReadingHandler getServerReadingHandler() {
        return serverReadingHandler;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public void setServerCommandManager(ServerCommandManager serverCommandManager) {
        this.serverCommandManager = serverCommandManager;
    }

    public ServerCommandManager getServerCommandManager() {
        return serverCommandManager;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    public ExecutorService getRequestResponserPool() {
        return requestResponserPool;
    }

    public ForkJoinPool getRequestProcessPool() {
        return requestProcessPool;
    }

    public ForkJoinPool getRequestReaderPool() {
        return requestReaderPool;
    }

    private void shutdownServerResources() { // Новый метод для корректного закрытия
        ServerMain.getLogger().log(Level.INFO, "Server is shutting down resources...");
        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
                ServerMain.getLogger().log(Level.INFO, "Selector closed.");
            }
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close();
                ServerMain.getLogger().log(Level.INFO, "ServerChannel closed.");
            }
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error closing server selector/channel", e);
        } finally {
            // Завершаем пулы потоков
            shutdownAndAwaitTermination(requestReaderPool, "RequestReaderPool");
            shutdownAndAwaitTermination(requestProcessPool, "RequestProcessPool");
            shutdownAndAwaitTermination(requestResponserPool, "RequestResponserPool");

            if (databaseManger != null) { // Проверка на null
                databaseManger.closeConnection();
                ServerMain.getLogger().log(Level.INFO, "DatabaseManger connection closed.");
            }
            ServerMain.getLogger().log(Level.INFO, "Server resources shut down complete.");
            // System.exit(1); // Не стоит делать System.exit здесь, если это не главный поток приложения
        }
    }

    // Вспомогательный метод для корректного завершения ExecutorService
    void shutdownAndAwaitTermination(ExecutorService pool, String poolName) {
        pool.shutdown(); // Запрещаем новые задачи
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) { // Ждем завершения текущих задач
                pool.shutdownNow(); // Отменяем ожидающие задачи
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    ServerMain.getLogger().log(Level.SEVERE, poolName + " did not terminate.");
                } else {
                    ServerMain.getLogger().log(Level.INFO, poolName + " terminated after shutdownNow.");
                }
            } else {
                ServerMain.getLogger().log(Level.INFO, poolName + " terminated normally.");
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    private void initMultiThreadReading(SelectionKey key) {
        Callable<UserRequest> toRead = serverReadingHandler.handleReading(key);
        if(toRead != null) {
            Future<UserRequest> toProcess = requestReaderPool.submit(toRead);
            Runnable processTask = () -> {
                UserRequest req = null;
                try {
                    req = toProcess.get();
                } catch (Exception e) {
                    UserRequest ureq = new UserRequest(null,null,null);
                    ureq.setUuid(UUID.fromString("-1"));
                    responseToClient(ureq, key,new Response(ResponseStatus.ERROR,"Exception occurred " + e.getMessage()));
                    return;
                }
                if(req != null) {
                    serverProcessHandler.handleRequest(key, req);
                }
            };
            requestProcessPool.submit(processTask);
        }

    }

    protected void responseToClient(UserRequest request, SelectionKey key, Response response) {
        Runnable doResponse = () -> {
            serverRespondingHandler.responseToClient(request, key,response);
        };
        requestResponserPool.submit(doResponse);
    }


}
