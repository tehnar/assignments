package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.String;

public class StringSetImpl implements StringSet, StreamSerializable {
    private final static int ALPHABET_SIZE = 2 * 26;
    private final static int UP_BYTE = 255;

    private static class StringSetNode {
        private StringSetNode[] goByChar;
        private boolean isTerminal;
        private int subtreeStringsCount;

        private StringSetNode() {
            goByChar = new StringSetNode[ALPHABET_SIZE];
            isTerminal = false;
            subtreeStringsCount = 0;
        }

        private static int charToIndex(char c) {
            if (c >= 'a' && c <= 'z') {
                return c - 'a';
            } else {
                return c - 'A' + 26;
            }
        }

        private StringSetNode goByIndex(int index) {
            if (goByChar[index] == null) {
                goByChar[index] = new StringSetNode();
            }

            return goByChar[index];
        }

        private StringSetNode goByChar(char c) {
            return goByIndex(charToIndex(c));
        }

        private boolean canGoByIndex(int index) {
            return goByChar[index] != null;
        }

        private boolean canGoByChar(char c) {
            return canGoByIndex(charToIndex(c));
        }

        private void setTerminal(boolean val) {
            if (val && !isTerminal) {
                subtreeStringsCount++;
            }
            else if (!val && isTerminal){
                subtreeStringsCount--;
            }
            isTerminal = val;
        }
    }

    private StringSetNode begin = new StringSetNode();

    private void serializeDFS(StringSetNode curNode, OutputStream out) throws IOException {
        out.write(curNode.isTerminal ? 1 : 0);
        for (int index = 0; index < ALPHABET_SIZE; index++) {
            if (curNode.canGoByIndex(index)) {
                out.write(index);
                serializeDFS(curNode.goByIndex(index), out);
            }
        }
        out.write(UP_BYTE);
    }

    @Override
    public void serialize(OutputStream out) {
        try {
            serializeDFS(begin, out);
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private int deserializeDFS(StringSetNode curNode, InputStream in) throws IOException {
        curNode.setTerminal(in.read() > 0);
        int val = in.read();
        while (val != UP_BYTE) {
            StringSetNode child = curNode.goByIndex(val);
            curNode.subtreeStringsCount += deserializeDFS(child, in);
            val = in.read();
        }
        return curNode.subtreeStringsCount;
    }

    @Override
    public void deserialize(InputStream in) {
        begin = new StringSetNode();
        try {
            deserializeDFS(begin, in);
        } catch (IOException exception) {
            throw new SerializationException();
        }
    }


    private StringSetNode processString(String str) {
        StringSetNode curNode = begin;
        for (char character : str.toCharArray()) {
            if (curNode.canGoByChar(character)) {
                curNode = curNode.goByChar(character);
            } else {
                return null;
            }
        }
        return curNode;
    }

    @Override
    public boolean add(String element) {
        if (contains(element)) {
            return false;
        }
        StringSetNode curNode = begin;
        for (char character : element.toCharArray()) {
            curNode.subtreeStringsCount++;
            curNode = curNode.goByChar(character);
        }
        curNode.setTerminal(true);
        return true;
    }

    @Override
    public boolean contains(String element) {
        StringSetNode node = processString(element);
        return node != null && node.isTerminal;
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }

        StringSetNode curNode = begin;
        for (char character : element.toCharArray()) {
            curNode.subtreeStringsCount--;
            curNode = curNode.goByChar(character);
        }
        curNode.setTerminal(false);
        return true;
    }

    @Override
    public int size() {
        return begin.subtreeStringsCount;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        StringSetNode node = processString(prefix);
        return node == null ? 0 : node.subtreeStringsCount;
    }
}

