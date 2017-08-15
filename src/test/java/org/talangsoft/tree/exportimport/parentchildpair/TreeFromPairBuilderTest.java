package org.talangsoft.tree.exportimport.parentchildpair;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.talangsoft.tree.Tree;
import org.talangsoft.tree.printer.TreePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeFromPairBuilderTest {


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


    protected <T> void verifyTreeNode(Tree<T> tree, T nodeData, T... expectedElements) {
        tree.lookup(nodeData)
                .map(node -> assertThat(node.getChildren()).containsOnly(expectedElements))
                .orElseThrow(() -> new RuntimeException(String.format("Node with data '%s' was not found", nodeData)));
    }

    @Test
    public void buildTreeFromRepeatedSortedPairsTest() {
        Tree<String> stringTree = TreeFromPairBuilder.buildFromRepeatedParentChildPairs(parentChildPairs);

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
    public void buildTreeFromRepeatedReversedPairsTest() {
        Tree<String> stringTree = TreeFromPairBuilder.buildFromRepeatedParentChildPairs(parentChildPairs.stream().collect(inReverse()));

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
    public void buildTreeFromSimplifiedPairsTest() {
        // referring to all children.
        Tree<String> stringTree = TreeFromPairBuilder.buildFromParentChildPairs(parentChildPairsSimplified.stream().collect(inReverse()));

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
        Tree<String> stringTree = TreeFromPairBuilder.buildFromRepeatedParentChildPairs(parentChildPairs);
        assertThat(stringTree.lookup("A").get().allBottomLevelSuccessor()).containsExactly("B", "F", "G", "E");
        assertThat(stringTree.lookup("B").get().allBottomLevelSuccessor()).containsExactly("B");
        assertThat(stringTree.lookup("C").get().allBottomLevelSuccessor()).containsExactly("F", "G", "E");
        assertThat(stringTree.lookup("D").get().allBottomLevelSuccessor()).containsExactly("F", "G");
        assertThat(stringTree.lookup("E").get().allBottomLevelSuccessor()).containsExactly("E");
        assertThat(stringTree.lookup("F").get().allBottomLevelSuccessor()).containsExactly("F");
        assertThat(stringTree.lookup("G").get().allBottomLevelSuccessor()).containsExactly("G");
    }


}
