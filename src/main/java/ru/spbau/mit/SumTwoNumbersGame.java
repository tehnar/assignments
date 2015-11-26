package ru.spbau.mit;

import java.util.*;


public class SumTwoNumbersGame implements Game {
    private final GameServer server;
    private int a, b;
    private Random randomGenerator = new Random();

    public SumTwoNumbersGame(GameServer server) {
        this.server = server;
        genNewNumbers();
    }

    private void genNewNumbers() {
        a = randomGenerator.nextInt(100);
        b = randomGenerator.nextInt(100);
        server.broadcast(String.format("%d %d", a, b));
    }

    @Override
    synchronized public void onPlayerConnected(String id) {
        server.sendTo(id, String.format("%d %d", a, b));
    }

    @Override
    synchronized public void onPlayerSentMsg(String id, String msg) {
        int answer = Integer.parseInt(msg);
        if (answer == a + b) {
            server.sendTo(id, "Right");
            server.broadcast(id + " won");
            genNewNumbers();

        } else {
            server.sendTo(id, "Wrong");
        }
    }
}
