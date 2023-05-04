package model.test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BinarySearchNode {

    private int value;
    private BinarySearchNode left;
    private BinarySearchNode right;

    public BinarySearchNode(int value, BinarySearchNode left, BinarySearchNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public int getValue() {
        return this.value;
    }

    public BinarySearchNode getLeft() {
        return this.left;
    }

    public BinarySearchNode getRight() {
        return this.right;
    }

    private StringBuilder stringRep(int level) {
        return new StringBuilder("_".repeat(level) + this.value + "\n")
                .append(this.left == null ?
                        "_".repeat(level + 1) + "\n" :
                        this.left.stringRep(level + 1))
                .append(this.right == null ?
                        "_".repeat(level + 1) + "\n" :
                        this.right.stringRep(level + 1));
    }

    @Override
    public String toString() {
        return this.stringRep(0).toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
           return false;
        }

        if (other instanceof BinarySearchNode otherNode) {
            return otherNode.value == otherNode.getValue()
            && Objects.equals(this.left, otherNode.getLeft())
                    && Objects.equals(this.right, otherNode.getRight());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.left, this.right);
    }

    /**
     * Insert the given node into the subtree rooted
     * by this BinarySearchNode.
     *
     * @param node The node to be inserted
     * @return The subtree resulting from this insert operation
     */
    public BinarySearchNode insert(BinarySearchNode node) {
        if (this.value >= node.value) {
            this.left = this.left == null ?
                    node : this.left.insert(node);
        } else {
            this.right = this.right == null ?
                    node : this.right.insert(node);
        }

        return this;
    }

    public BinarySearchNode(List<Integer> values) {
        if (values != null && !values.isEmpty()) {
            Collections.sort(values);
            BinarySearchNode constructed = buildTree(values, 0, values.size());
            if (constructed != null) {
                this.value = constructed.value;
                this.left = constructed.left;
                this.right = constructed.right;
            }
        }
    }

    private static BinarySearchNode buildTree(List<Integer> values, int low, int high) {
        if (low >= high) {
            return null;
        }

        int mid = (low + high) / 2;

        BinarySearchNode left = buildTree(values, low, mid);
        BinarySearchNode right = buildTree(values, mid + 1, high);
        return new BinarySearchNode(values.get(mid), left, right);
    }
}