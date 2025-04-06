package ru.bright.server;


import ru.bright.Main;
import ru.bright.Response;
import ru.bright.ResponseStatus;
import ru.bright.managers.CollectionManager;
import ru.bright.managers.ServerCommandManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
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
    private Logger logger;
    private CollectionManager collectionManager;

    public Server(int portNumber) {
        this.portNumber = portNumber;
        this.serverConnectionHandler = new ServerConnectionHandler(this);
        this.serverReadingHandler = new ServerReadingHandler(this);
        this.serverRespondingHandler = new ServerRespondingHandler(this);
    }

    public void startServer() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(portNumber));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            Main.getLogger().log(Level.INFO,"Server listening on port: " + portNumber);
            while(true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isAcceptable()) {
                        serverConnectionHandler.handleNewConnection(key,serverChannel, selector);
                    }
                    if(key.isReadable()) {
                        try {
                            serverReadingHandler.handleReading(key);
                        } catch (IOException e) {
                            getServerCommandManager().saveFile();
                            Main.getLogger().log(Level.SEVERE, "Error while client reading", e.getMessage());
                            key.cancel();
                        }
                    }
                }
            }
        } catch (IOException e) {
            Main.getLogger().log(Level.SEVERE, "Error working server socket", e.getMessage());
        } finally {
            try {
                if (serverChannel != null && serverChannel.isOpen()) {
                    serverChannel.close();
                }
                if(selector != null && selector.isOpen()) {
                    selector.close();
                }
            } catch (IOException e) {
                Main.getLogger().log(Level.SEVERE, "Error closing server socket");
            }
        }
    }

    protected void responseToClient(SelectionKey key, Response response) {
        serverRespondingHandler.responseToClient(key,response);
    }

    protected void responseToClient(SelectionKey key, SocketChannel clientChannel, Response response) {
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
            shutdown();
        } finally {

        }
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
            Main.getLogger().log(Level.INFO, "Server is shutting down");
        } catch (IOException e) {
            Main.getLogger().log(Level.SEVERE, "Error while shutting down server", e);
        } finally {
            System.exit(1);
        }
    }

    public void sendError(SelectionKey key, String message) {
        responseToClient(key,new Response(ResponseStatus.ERROR,message));
    }

    public void sendOK(SelectionKey key, String message) {
        responseToClient(key,new Response(ResponseStatus.OK,message));
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

}
