package org.talangsoft.tree;

import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeTest {

    /**
     * Representing pairs for:
     * |
     * |    A
     * |  /   \
     * | B     C
     * |      / \
     * |     D   E
     * |    / \
     * |   F  G
     */
    private List<ParentChildPair<String>> parentChildPairs = Arrays.asList(
            new ParentChildPair("A", "B"),
            new ParentChildPair("A", "C"),
            new ParentChildPair("A", "D"),
            new ParentChildPair("A", "E"),
            new ParentChildPair("A", "F"),
            new ParentChildPair("A", "G"),
            new ParentChildPair("C", "D"),
            new ParentChildPair("C", "E"),
            new ParentChildPair("C", "F"),
            new ParentChildPair("C", "G"),
            new ParentChildPair("D", "F"),
            new ParentChildPair("D", "G")
    );
    private List<ParentChildPair<String>> parentChildPairsSimplified = Arrays.asList(
            new ParentChildPair("A", "B"),
            new ParentChildPair("A", "C"),
            new ParentChildPair("C", "D"),
            new ParentChildPair("C", "E"),
            new ParentChildPair("D", "F"),
            new ParentChildPair("D", "G")
    );

    public static <T> Collector<T, List<T>, List<T>> inReverse() {
        return Collector.of(
                ArrayList::new,
                (l, t) -> l.add(t),
                (l, r) -> {
                    l.addAll(r);
                    return l;
                },
                Lists::<T>reverse);
    }

    @Test
    public void lookupShouldReturnEmptyIfElementNotFound() {
        Tree<String> stringTree = new Tree<String>("A", Arrays.asList(new Tree("B"), new Tree("C")));
        assertThat(stringTree.lookup("A").isPresent());
        assertThat(stringTree.lookup("B").isPresent());
        assertThat(stringTree.lookup("C").isPresent());
        assertThat(!stringTree.lookup("D").isPresent());
        assertThat(!stringTree.lookup("E").isPresent());
    }

    protected <T> void verifyTreeNode(Tree<T> tree, T nodeData, T... expectedElements) {
        tree.lookup(nodeData)
                .map(node -> assertThat(node.getChildren()).containsOnly(expectedElements))
                .orElseThrow(() -> new RuntimeException(String.format("Node with data '%s' was not found", nodeData)));
    }

    @Test
    public void buildTreeFromSortedPairsTest() {
        Tree<String> stringTree = TreeFromPairBuilder.build(parentChildPairs);

        /*
         * Verify structure:
         * |    A
         * |  /   \
         * | B     C
         * |      / \
         * |     D   E
         * |    / \
         * |   F  G
         */
        verifyTreeNode(stringTree, "A", new String[]{"B", "C"});
        verifyTreeNode(stringTree, "B", new String[]{});
        verifyTreeNode(stringTree, "C", new String[]{"D", "E"});
        verifyTreeNode(stringTree, "D", new String[]{"F", "G"});
        verifyTreeNode(stringTree, "E", new String[]{});
        verifyTreeNode(stringTree, "F", new String[]{});
        verifyTreeNode(stringTree, "G", new String[]{});

        // print it to see
        TreePrinter.print(stringTree);
    }

    @Test
    public void buildTreeFromReversedPairsTest() {
        Tree<String> stringTree = TreeFromPairBuilder.build(parentChildPairs.stream().collect(inReverse()));

        verifyTreeNode(stringTree, "A", new String[]{"B", "C"});
        verifyTreeNode(stringTree, "B", new String[]{});
        verifyTreeNode(stringTree, "C", new String[]{"D", "E"});
        verifyTreeNode(stringTree, "D", new String[]{"F", "G"});
        verifyTreeNode(stringTree, "E", new String[]{});
        verifyTreeNode(stringTree, "F", new String[]{});
        verifyTreeNode(stringTree, "G", new String[]{});

        // print it to see
        TreePrinter.print(stringTree);
    }

    @Test
    @Ignore
    public void buildTreeFromSimplifiedPairsTest() {
        // TODO: fixme: it won't work because we are ordering based on occerences and that assumes that the topmost parent
        // referring to all children.
        Tree<String> stringTree = TreeFromPairBuilder.build(parentChildPairsSimplified.stream().collect(inReverse()));

        verifyTreeNode(stringTree, "A", new String[]{"B", "C"});
        verifyTreeNode(stringTree, "B", new String[]{});
        verifyTreeNode(stringTree, "C", new String[]{"D", "E"});
        verifyTreeNode(stringTree, "D", new String[]{"F", "G"});
        verifyTreeNode(stringTree, "E", new String[]{});
        verifyTreeNode(stringTree, "F", new String[]{});
        verifyTreeNode(stringTree, "G", new String[]{});

        // print it to see
        TreePrinter.print(stringTree);
    }

    @Test
    public void getAllBottomLevelSuccessorForANodeShouldReturnAllSuccessorOnDifferentBranches() {
        /*
         * |    A
         * |  /   \
         * | B     C
         * |      / \
         * |     D   E
         * |    / \
         * |   F  G
         */
        Tree<String> stringTree = TreeFromPairBuilder.build(parentChildPairs);
        assertThat(stringTree.lookup("A").get().allBottomLevelSuccessor()).containsExactly("B", "F", "G", "E");
        assertThat(stringTree.lookup("B").get().allBottomLevelSuccessor()).containsExactly("B");
        assertThat(stringTree.lookup("C").get().allBottomLevelSuccessor()).containsExactly("F", "G", "E");
        assertThat(stringTree.lookup("D").get().allBottomLevelSuccessor()).containsExactly("F", "G");
        assertThat(stringTree.lookup("E").get().allBottomLevelSuccessor()).containsExactly("E");
        assertThat(stringTree.lookup("F").get().allBottomLevelSuccessor()).containsExactly("F");
        assertThat(stringTree.lookup("G").get().allBottomLevelSuccessor()).containsExactly("G");
    }

    @Test
    public void getAllElementsShouldReturnAllElements() {
        Tree<String> stringTree = TreeFromPairBuilder.build(parentChildPairs);
        assertThat(stringTree.allElements()).containsExactly("A", "B", "C", "D", "F", "G", "E");
    }

    @Test
    public void getAllNodesShouldReturnAllNodes() {
        Tree<String> stringTree = TreeFromPairBuilder.build(parentChildPairs);
        List<Tree<String>> nodes = stringTree.allNodes();
        List<String> elementsFromNodes = nodes.stream().map(node -> node.getData()).collect(Collectors.toList());
        assertThat(elementsFromNodes).containsExactly("A", "B", "C", "D", "F", "G", "E");
    }
}
