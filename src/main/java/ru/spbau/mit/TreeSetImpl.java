package ru.spbau.mit;

import java.util.*;

public class TreeSetImpl<E> extends AbstractSet<E> {
    public class TreeSetIterator implements Iterator<E>{
        private TreeSetNode curNode, lastNode;

        public TreeSetIterator() {
            curNode = leftmost(root);
            lastNode = null;
        }

        @Override
        public boolean hasNext() {
            return curNode != null;
        }

        @Override
        public E next() {
            if (curNode == null)
                throw new NoSuchElementException();
            lastNode = curNode;
            curNode = goRight(curNode);
            return lastNode.value;
        }

        @Override
        public void remove() {
            if (lastNode == null) {
                throw new IllegalStateException();
            }
            TreeSetImpl.this.remove(lastNode.value);
            lastNode = null;
        }
    }

    private class TreeSetNode {
        TreeSetNode left = null, right = null, parent = null;
        int height = new Random().nextInt();
        E value = null;

        TreeSetNode(E val) {
            value = val;
        }


        void updateParents() {
            parent = null;
            if (left != null) {
                left.parent = this;
            }
            if (right != null) {
                right.parent = this;
            }
        }
    }

    private class PairOfNodes {
        public TreeSetNode first = null, second = null;
        PairOfNodes(TreeSetNode a, TreeSetNode b) {
            first = a;
            second = b;
        }
    }

    private Comparator <E> comparator = null;
    private int size = 0;
    private TreeSetNode root = null;

    private TreeSetNode merge(TreeSetNode left, TreeSetNode right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }

        if (left.height > right.height) {
            left.right = merge(left.right, right);
            left.updateParents();
            return left;
        } else {
            right.left = merge(left, right.left);
            right.updateParents();
            return right;
        }
    }

    private PairOfNodes split(TreeSetNode root, E val) {
        if (root == null)
            return new PairOfNodes(null, null);

        if (comparator.compare(root.value, val) > 0) {
            PairOfNodes tmp = split(root.left, val);
            root.left = tmp.second;
            root.updateParents();
            return new PairOfNodes(tmp.first, root);
        } else {
            PairOfNodes tmp = split(root.right, val);
            root.right = tmp.first;
            root.updateParents();
            return new PairOfNodes(root, tmp.second);
        }
    }

    private TreeSetNode leftmost(TreeSetNode root) {
        if (root == null) {
            return null;
        }
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    private TreeSetNode rightmost(TreeSetNode root) {
        if (root == null) {
            return null;
        }
        while (root.right != null) {
            root = root.right;
        }
        return root;
    }

    private TreeSetNode goRight(TreeSetNode root) {
        if (root == null)
            return null;

        if (root.right != null) {
            return leftmost(root.right);
        }

        while (root.parent != null && root.parent.right == root) {
            root = root.parent;
        }

        if (root.parent == null) {
            return null;
        }
        return root.parent;
    }

    public TreeSetImpl(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean contains(Object val) {
        PairOfNodes left = split(root, (E) val);
        TreeSetNode leftMax = rightmost(left.first);
        root = merge(left.first, left.second);
        return leftMax != null && comparator.compare(leftMax.value, (E) val) == 0;
    }

    @Override
    public boolean add(E val) {
        PairOfNodes left = split(root, val);
        TreeSetNode leftMax = rightmost(left.first);
        if (leftMax != null && comparator.compare(leftMax.value, val) == 0) {
            root = merge(left.first, left.second);
            return false;
        }
        left.first = merge(left.first, new TreeSetNode(val));
        root = merge(left.first, left.second);
        size++;
        return true;
    }
    private TreeSetNode removeRecursive(TreeSetNode root, E val) {
        if (root == null) {
            return null;
        }
        int resultOfComparing = comparator.compare(root.value, val);
        if (resultOfComparing == 0) {
            return merge(root.left, root.right);
        } else if (resultOfComparing > 0) {
            root.left = removeRecursive(root.left, val);
        } else {
            root.right = removeRecursive(root.right, val);
        }

        root.updateParents();
        return root;
    }

    public boolean remove(Object val) {
        boolean result = contains(val);
        if (result) {
            root = removeRecursive(root, (E) val);
            size--;
        }
        return result;
    }

    @Override
    public Iterator<E> iterator() {
        return new TreeSetIterator();
    }

    @Override
    public int size() {
        return size;
    }
}
