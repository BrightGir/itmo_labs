package ru.bright.server;


import ru.bright.*;
import ru.bright.managers.AuthManager;
import ru.bright.managers.CollectionManager;
import ru.bright.managers.DatabaseManger;
import ru.bright.managers.ServerCommandManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
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
            ServerMain.getLogger().log(Level.INFO,"Server listening on port: " + portNumber);
            while(true) {
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
                                initMultiThreadReading(key);
                            } catch (IOException e) {
                                ServerMain.getLogger().log(Level.SEVERE, "Error while client reading", e.getMessage());
                                key.cancel();
                            }
                        }
                    }
                } catch (CancelledKeyException e) {

                }
            }
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error working server socket", e.getMessage());
        } finally {
            try {
                if (serverChannel != null && serverChannel.isOpen()) {
                    serverChannel.close();
                }
                if(selector != null && selector.isOpen()) {
                    selector.close();
                }
            } catch (IOException e) {
                ServerMain.getLogger().log(Level.SEVERE, "Error closing server socket");
            }
        }
    }



    private void initMultiThreadReading(SelectionKey key) throws IOException {
        Callable<UserRequest> toRead = serverReadingHandler.handleReading(key);
        if(toRead != null) {
            Future<UserRequest> toProcess = requestReaderPool.submit(toRead);
            Runnable processTask = () -> {
                UserRequest req = null;
                try {
                    req = toProcess.get();
                } catch (Exception e) {
                    responseToClient(key,new Response(ResponseStatus.ERROR,"Exception occurred " + e.getMessage()));
                    return;
                }
                if(req != null) {
                    serverProcessHandler.handleRequest(key, req);
                }
            };
            requestProcessPool.submit(processTask);
        }

    }

    protected void responseToClient(SelectionKey key, Response response) {
        Runnable doResponse = () -> {
            serverRespondingHandler.responseToClient(key,response);
        };
        requestResponserPool.submit(doResponse);
    }

    protected void responseToClient(SelectionKey key, SocketChannel clientChannel, Response response) {
        Runnable doResponse = () -> {
            serverRespondingHandler.responseToClient(key,clientChannel, response);
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

    public void sendError(SelectionKey key, String message) {
        responseToClient(key,new Response(ResponseStatus.ERROR,message));
    }

    public void sendOK(SelectionKey key, String message) {
        responseToClient(key,new Response(ResponseStatus.OK,message));
    }

    public void sendAnother(SelectionKey key, ResponseStatus status, String message) {
        responseToClient(key,new Response(status,message));
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
}
