package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class GameServerImpl implements GameServer {
    private final ArrayList<Connection> connections = new ArrayList<>();
    private Game gameClassInstance;
    private Class <?> gameClass;
    private final ArrayList <LinkedBlockingQueue<String>> messageQueue = new ArrayList<>();
    private static boolean isInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '-') {
                if (i != 0 || str.length() == 1) {
                    return false;
                }
            } else {
                char c = str.charAt(i);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
        }
        return true;
    }

    public GameServerImpl(String gameClassName, Properties properties) {
        try {
            gameClass = Class.forName(gameClassName);
            gameClassInstance = (Game) gameClass.getConstructor(GameServer.class).newInstance(this);
            Set<String> propertyNames = properties.stringPropertyNames();
            for (String propertyName : propertyNames) {
                String property = properties.getProperty(propertyName);
                String setterName = "set" + (char) (propertyName.charAt(0) + 'A' - 'a') + propertyName.substring(1);
                if (isInteger(property)) {
                    gameClass.getMethod(setterName, int.class).invoke(gameClassInstance, Integer.parseInt(property));
                } else {
                    gameClass.getMethod(setterName, String.class).invoke(gameClassInstance, property);
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException |
                InvocationTargetException e) {
            e.printStackTrace();
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

                new Thread(new Runnable() {
                    int myId = id;
                    Connection myConnection = connection;

                    @Override
                    public void run() {
                        while (true) {
                            String message = null;
                            try {
                                message = messageQueue.get(myId).take();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            synchronized (myConnection) {
                                if (myConnection.isClosed()) {
                                    return;
                                }
                                myConnection.send(message);
                            }
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    Connection myConnection = connection;
                    String myId = idStr;

                    @Override
                    public void run() {
                        while (true) {
                            try {
                                synchronized (myConnection) {
                                    if (myConnection.isClosed()) {
                                        return;
                                    }
                                    String message = myConnection.receive(1);
                                    if (message != null) {
                                        gameClassInstance.onPlayerSentMsg(myId, message);
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
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
            e.printStackTrace();
        }
    }
}
