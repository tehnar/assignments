package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Override;
import java.lang.String;
import java.util.Stack;

public class StringSetImpl implements StringSet, StreamSerializable {
    final static int ALPHABET_SIZE = 2 * 26;
    class StringSetNode {
        private StringSetNode[] go;
        private boolean isTerminal;
        private int subtreeStringsCount;

        public StringSetNode() {
            go = new StringSetNode[ALPHABET_SIZE];
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
        public StringSetNode go(int index) {
            if (go[index] == null) {
                go[index] = new StringSetNode();
            }

            return go[index];
        }

        public StringSetNode go(char c) {
            return go(charToIndex(c));
        }

        public boolean canGo(int index) {
            return go[index] != null;
        }

        public boolean canGo(char c) {
            return canGo(charToIndex(c));
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

    @Override
    public void serialize(OutputStream out) {
        Stack<StringSetNode> stackOfNodes = new Stack<>();
        Stack<Integer> stackOfIndices = new Stack<>();
        stackOfNodes.push(begin);
        stackOfIndices.push(0);
        try {
            out.write(begin.isTerminal ? 1 : 0);
            while (!stackOfNodes.empty()) {
                StringSetNode curNode = stackOfNodes.pop();
                for (int index = stackOfIndices.pop(); index < ALPHABET_SIZE; index++) {
                    if (curNode.canGo(index)) {
                        out.write(index);
                        StringSetNode nextNode = curNode.go(index);
                        out.write(nextNode.isTerminal ? 1 : 0);
                        stackOfIndices.push(index + 1);
                        stackOfNodes.push(curNode);
                        stackOfNodes.push(nextNode);
                        stackOfIndices.push(0);
                        break;
                    }
                    if (index == ALPHABET_SIZE - 1) {
                        out.write(255);
                    }
                }
            }
        }
        catch (IOException e) {
            throw new SerializationException();
        }
    }

    @Override
    public void deserialize(InputStream in) {
        begin = new StringSetNode();
        Stack<StringSetNode> stackOfNodes = new Stack<>();
        stackOfNodes.push(begin);
        try {
            begin.setTerminal(in.read() > 0);
            while (!stackOfNodes.empty()) {
                int val = in.read();
                if (val == 255) {
                    int substringCnt = stackOfNodes.pop().subtreeStringsCount;
                    if (!stackOfNodes.empty()) {
                        stackOfNodes.peek().subtreeStringsCount += substringCnt;
                    }
                } else {
                    StringSetNode node = stackOfNodes.peek().go(val);
                    node.setTerminal(in.read() > 0);
                    stackOfNodes.push(node);
                }
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private StringSetNode processString(String str) {
        StringSetNode curNode = begin;
        for (int i = 0; i < str.length(); i++) {
            if (curNode.canGo(str.charAt(i))) {
                curNode = curNode.go(str.charAt(i));
            } else {
                return null;
            }
        }
        return curNode;
    }

    @Override
    public boolean add(String element) {
        StringSetNode curNode = begin;
        for (int i = 0; i < element.length(); i++) {
            curNode = curNode.go(element.charAt(i));
        }
        boolean result = !curNode.isTerminal;
        curNode.setTerminal(true);

        if (result) {
            curNode = begin;
            for (int i = 0; i < element.length(); i++) {
                curNode.subtreeStringsCount++;
                curNode = curNode.go(element.charAt(i));
            }
        }
        return result;
    }

    @Override
    public boolean contains(String element) {
        StringSetNode node = processString(element);
        return node == null ? false : node.isTerminal;

    }

    @Override
    public boolean remove(String element) {
        boolean result = contains(element);
        if (!result)
            return false;

        StringSetNode curNode = begin;
        for (int i = 0; i < element.length(); i++) {
            curNode.subtreeStringsCount--;
            curNode = curNode.go(element.charAt(i));
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

