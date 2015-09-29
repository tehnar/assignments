package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.String;

public class StringSetImpl implements StringSet, StreamSerializable {
    private final static int ALPHABET_SIZE = 2 * 26;
    private static class StringSetNode {
        private StringSetNode[] goByChar;
        private boolean isTerminal;
        private int subtreeStringsCount;

        public StringSetNode() {
            goByChar = new StringSetNode[ALPHABET_SIZE];
            isTerminal = false;
            subtreeStringsCount = 0;
        }

        private int charToIndex(char c) {
            if (c >= 'a' && c <= 'z') {
                return c - 'a';
            } else {
                return c - 'A' + 26;
            }
        }
        public StringSetNode goByIndex(int index) {
            if (goByChar[index] == null) {
                goByChar[index] = new StringSetNode();
            }

            return goByChar[index];
        }

        public StringSetNode goByChar(char c) {
            return goByIndex(charToIndex(c));
        }

        public boolean canGoByIndex(int index) {
            return goByChar[index] != null;
        }

        public boolean canGoByChar(char c) {
            return canGoByIndex(charToIndex(c));
        }

        public void setTerminal(boolean val) {
            if (val && !isTerminal) {
                subtreeStringsCount++;
            }
            else if (!val && isTerminal){
                subtreeStringsCount--;
            }
            isTerminal = val;
        }
    }

    private StringSetNode begin;

    StringSetImpl() {
        begin = new StringSetNode();
    }

    private void serializeDFS(StringSetNode curNode, OutputStream out) {
        try {
            out.write(curNode.isTerminal ? 1 : 0);
            for (int index = 0; index < ALPHABET_SIZE; index++) {
                if (curNode.canGoByIndex(index)) {
                    out.write(index);
                    serializeDFS(curNode.goByIndex(index), out);
                }
            }
            out.write(255);
        } catch (IOException e) {
            throw new SerializationException();
        }
    }
    @Override
    public void serialize(OutputStream out) {
        serializeDFS(begin, out);
    }

    private int deserializeDFS(StringSetNode curNode, InputStream in) {
        try {
            curNode.setTerminal(in.read() > 0);
            int val = in.read();
            while (val != 255) {
                StringSetNode child = curNode.goByIndex(val);
                curNode.subtreeStringsCount  += deserializeDFS(child, in);
                val = in.read();
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
        return curNode.subtreeStringsCount;
    }

    @Override
    public void deserialize(InputStream in) {
        deserializeDFS(begin = new StringSetNode(), in);
    }

    private StringSetNode processString(String str) {
        StringSetNode curNode = begin;
        for (int i = 0; i < str.length(); i++) {
            if (curNode.canGoByChar(str.charAt(i))) {
                curNode = curNode.goByChar(str.charAt(i));
            } else {
                return null;
            }
        }
        return curNode;
    }

    @Override
    public boolean add(String element) {
        boolean result = contains(element);
        if (!result) {
            StringSetNode curNode = begin;
            for (int i = 0; i < element.length(); i++) {
                curNode.subtreeStringsCount++;
                curNode = curNode.goByChar(element.charAt(i));
            }
            curNode.setTerminal(true);
        }
        return !result;
    }

    @Override
    public boolean contains(String element) {
        StringSetNode node = processString(element);
        return node != null && node.isTerminal;

    }

    @Override
    public boolean remove(String element) {
        boolean result = contains(element);
        if (!result) {
            return false;
        }

        StringSetNode curNode = begin;
        for (int i = 0; i < element.length(); i++) {
            curNode.subtreeStringsCount--;
            curNode = curNode.goByChar(element.charAt(i));
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

