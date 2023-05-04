package model;

import model.test.BinarySearchNode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class BinarySearchNodeTest {

    @Test
    void testBuildTree() {
        List<Integer> values = Arrays.asList(5, 8, 1, 0, 3);
        BinarySearchNode root = new BinarySearchNode(values);

        BinarySearchNode expected = new BinarySearchNode(3,
                new BinarySearchNode(1,
                        new BinarySearchNode(0, null, null),
                        null),
                new BinarySearchNode(8,
                        new BinarySearchNode(5, null, null),
                        null));

        assertThat(root).isEqualTo(expected);
    }

    @Test
    void testInsertLeft() {
        BinarySearchNode root = new BinarySearchNode(10, null, null);
        root.insert(new BinarySearchNode(3, null, null));

        BinarySearchNode expected = new BinarySearchNode(10,
                new BinarySearchNode(3, null, null),
                null);
        assertThat(root).isEqualTo(expected);
    }

    @Test
    void testInsertRight() {
        BinarySearchNode root = new BinarySearchNode(10, null, null);
        root.insert(new BinarySearchNode(3, null, null));
        root.insert(new BinarySearchNode(12, null, null));

        BinarySearchNode expected = new BinarySearchNode(10,
                new BinarySearchNode(3, null, null),
                new BinarySearchNode(12, null, null));

        assertThat(root).isEqualTo(expected);
    }

    @Test
    void testInsertLeftRightRight() {
        BinarySearchNode root = new BinarySearchNode(10, null, null);
        root.insert(new BinarySearchNode(3, null, null));
        root.insert(new BinarySearchNode(12, null, null));
        root.insert(new BinarySearchNode(4, null, null));

        BinarySearchNode expected = new BinarySearchNode(10,
                new BinarySearchNode(3,
                        null,
                        new BinarySearchNode(4, null, null)),
                new BinarySearchNode(12, null, null));

        assertThat(root).isEqualTo(expected);
    }

    @Test
    void testInsertFarRight() {
        BinarySearchNode root = new BinarySearchNode(10, null, null);
        root.insert(new BinarySearchNode(3, null, null));
        root.insert(new BinarySearchNode(12, null, null));
        root.insert(new BinarySearchNode(4, null, null));
        root.insert(new BinarySearchNode(13, null, null));

        BinarySearchNode expected = new BinarySearchNode(10,
                new BinarySearchNode(3,
                        null,
                        new BinarySearchNode(4, null, null)),
                new BinarySearchNode(12,
                        null,
                        new BinarySearchNode(13, null, null)));

        assertThat(root).isEqualTo(expected);
    }

    @Test
    void testInsertFarRightThenLeft() {
        BinarySearchNode root = new BinarySearchNode(10, null, null);
        root.insert(new BinarySearchNode(3, null, null));
        root.insert(new BinarySearchNode(12, null, null));
        root.insert(new BinarySearchNode(4, null, null));
        root.insert(new BinarySearchNode(13, null, null));
        root.insert(new BinarySearchNode(11, null, null));

        BinarySearchNode expected = new BinarySearchNode(10,
                new BinarySearchNode(3,
                        null,
                        new BinarySearchNode(4, null, null)),
                new BinarySearchNode(12,
                        new BinarySearchNode(11, null, null),
                        new BinarySearchNode(13, null, null)));

        assertThat(root).isEqualTo(expected);
    }
}