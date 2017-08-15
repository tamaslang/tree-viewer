package org.talangsoft.tree;

import org.junit.Before;
import org.junit.Test;
import org.talangsoft.tree.exportimport.parentchildpair.ParentChildPair;
import org.talangsoft.tree.exportimport.parentchildpair.TreeFromPairBuilder;

import java.util.Arrays;
import java.util.List;
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
            new ParentChildPair("C", "D"),
            new ParentChildPair("C", "E"),
            new ParentChildPair("D", "F"),
            new ParentChildPair("D", "G")
    );

    private Tree<String> exampleTree;

    @Before
    public void setUp() {
        exampleTree = TreeFromPairBuilder.buildFromParentChildPairs(parentChildPairs);
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

    @Test
    public void getAllElementsShouldReturnAllElements() {
        assertThat(exampleTree.allElements()).containsExactlyInAnyOrder("A", "B", "C", "D", "F", "G", "E");
    }

    @Test
    public void getAllNodesShouldReturnAllNodes() {
        List<Tree<String>> nodes = exampleTree.allNodes();
        List<String> elementsFromNodes = nodes.stream().map(node -> node.getData()).collect(Collectors.toList());
        assertThat(elementsFromNodes).containsExactlyInAnyOrder("A", "B", "C", "D", "F", "G", "E");
    }
}
