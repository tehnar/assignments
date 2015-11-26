package ru.spbau.mit;

import java.io.*;
import java.util.*;


public class QuizGame implements Game {
    private static final String START_MESSAGE = "!start";
    private static final String STOP_MESSAGE = "!stop";
    private static final String LETTER_CNT = "(%d letters)";
    private static final String NEXT_ROUND = "New round started:";
    private static final String CURRENT_PREFIX = "Current prefix is";
    private static final String TIME_LEFT = "Nobody guessed, the word was";
    private static final String STOP_GAME = "Game has been stopped by";
    private static final String WINNER = "The winner is";
    private static final String WRONG = "Wrong try";

    private GameServer server;
    private int questionIndex = -1, currentLetter, delayTime, maxLettersToOpen;
    private ArrayList<String> questions = null, answers = null;
    private Thread questionTimer = null;

    public QuizGame(GameServer server) {
        this.server = server;
    }

    @Override
    public void onPlayerConnected(String id) {

    }

    public void setDictionaryFilename(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            questions = new ArrayList<>();
            answers = new ArrayList<>();

            while (true) {
                String str = reader.readLine();
                if (str == null) {
                    break;
                }
                String [] parts = str.split(";");
                questions.add(parts[0]);
                answers.add(parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDelayUntilNextLetter(int delay) {
        this.delayTime = delay;
    }

    public void setMaxLettersToOpen(int letterCnt) {
        this.maxLettersToOpen = letterCnt;
    }

    private boolean showNextLetter() {
        if (currentLetter == maxLettersToOpen) {
            return false;
        }
        currentLetter++;
        server.broadcast(CURRENT_PREFIX + " " + answers.get(questionIndex).substring(0, currentLetter));
        return true;
    }

    private void timeLeft() {
        server.broadcast(TIME_LEFT + " " + answers.get(questionIndex));
        nextQuestion();
    }

    private String letterCnt(String str) {
        return String.format(LETTER_CNT, str.length());
    }

    private void startGame() {
        nextQuestion();
    }

    private void nextQuestion() {
        if (questionTimer != null) {
            questionTimer.interrupt();
        }

        questionIndex = (questionIndex + 1) % questions.size();
        currentLetter = 0;
        server.broadcast(NEXT_ROUND + " " + questions.get(questionIndex) +
                " " + letterCnt(answers.get(questionIndex)));
        questionTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(delayTime);
                    } catch (InterruptedException e) {
                        return;
                    }
                    synchronized (QuizGame.this) {
                        if (Thread.interrupted()) {
                            return;
                        }

                        if (!showNextLetter()) {
                            timeLeft();
                            return;
                        }
                    }
                }
            }
        });
        questionTimer.start();
    }

    private void stopGame(String whoStopped) {
        server.broadcast(STOP_GAME + " " + whoStopped);
    }

    @Override
    synchronized public void onPlayerSentMsg(String id, String msg) {
        System.err.println("QuizGame: processing message: " + msg);
        if (msg.equals(START_MESSAGE)) {
            startGame();
        } else if (msg.equals(STOP_MESSAGE)) {
            stopGame(id);
        } else {
            if (msg.equals(answers.get(questionIndex))) {
                server.broadcast(WINNER + " " + id);
                nextQuestion();
            } else {
                server.sendTo(id, WRONG);
            }
        }
    }
}
