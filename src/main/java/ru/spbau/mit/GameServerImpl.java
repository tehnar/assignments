package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class GameServerImpl implements GameServer {
    private final ArrayList<Connection> connections = new ArrayList<>();
    private final Game gameClassInstance;
    private final ArrayList <LinkedBlockingQueue<String> > messageQueue = new ArrayList<>();

    public GameServerImpl(String gameClassName, Properties properties) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        final Class <?> gameClass = Class.forName(gameClassName);
        gameClassInstance = (Game) gameClass.getConstructor(GameServer.class).newInstance(this);
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String propertyName : propertyNames) {
            String property = properties.getProperty(propertyName);
            String setterName = "set" + (char) (propertyName.charAt(0) + 'A' - 'a') + propertyName.substring(1);
            Integer num ;
            try {
                num = Integer.parseInt(property);
            } catch (NumberFormatException e) {
                num = null;
            }
            if (num != null) {
                gameClass.getMethod(setterName, int.class).invoke(gameClassInstance, num.intValue());
            } else {
                gameClass.getMethod(setterName, String.class).invoke(gameClassInstance, property);
            }
        }
    }

    private class ConnectionHandler implements Runnable {
        private final Connection connection;
        private final int id;
        private final String idStr;

        ConnectionHandler(int id, Connection connection) {
            this.id = id;
            this.connection = connection;
            this.idStr = Integer.toString(id);
        }

        @Override
        public void run() {
            while (true) {
                String message = messageQueue.get(id).poll();
                if (message != null) {
                    if (connection.isClosed()) {
                        return;
                    }
                    connection.send(message);
                }
                if (connection.isClosed()) {
                    return;
                }
                try {
                    message = connection.receive(10);
                } catch (InterruptedException e) {
                }
                if (message != null) {
                    gameClassInstance.onPlayerSentMsg(idStr, message);
                }

            }

        }
    }
    @Override
    public void accept(final Connection connection) {
        final int id = connections.size();
        connections.add(connection);
        messageQueue.add(new LinkedBlockingQueue<String>());
        final String idStr = Integer.toString(id);
        sendTo(idStr, idStr);

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameClassInstance.onPlayerConnected(idStr);
                new Thread(new ConnectionHandler(id, connection)).start();
            }

        }).start();
    }

    @Override
    public void broadcast(String message) {
        for (int id = 0; id < connections.size(); id++) {
            sendTo(Integer.toString(id), message);
        }
    }

    @Override
    public void sendTo(String id, String message) {
        int curId = Integer.parseInt(id);
        try {
            messageQueue.get(curId).put(message);
        } catch (InterruptedException e) {
        }
    }
}
