package org.talangsoft.tree.exportimport.json;

import org.junit.Test;
import org.talangsoft.tree.ParentChildPair;
import org.talangsoft.tree.Tree;
import org.talangsoft.tree.TreeFromPairBuilder;
import org.talangsoft.tree.exportimport.TreeElement;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeJsonExportImportTest {
    /*
     * |    A
     * |  /   \
     * | B     C
     * |      / \
     * |     D   E
     * |    / \
     * |   F  G
     */
    private List<ParentChildPair<Character>> parentChildPairs = Arrays.asList(
            new ParentChildPair('A', 'B'),
            new ParentChildPair('A', 'C'),
            new ParentChildPair('A', 'D'),
            new ParentChildPair('A', 'E'),
            new ParentChildPair('A', 'F'),
            new ParentChildPair('A', 'G'),
            new ParentChildPair('C', 'D'),
            new ParentChildPair('C', 'E'),
            new ParentChildPair('C', 'F'),
            new ParentChildPair('C', 'G'),
            new ParentChildPair('D', 'F'),
            new ParentChildPair('D', 'G')
    );

    @Test
    public void exportCharacterTree() {
        Tree<Character> charTree = TreeFromPairBuilder.build(parentChildPairs);
        // map characters to their integer representation
        List<TreeElement<Integer, Character>> exported = TreeJsonExporter.exportTree(charTree, Character::getNumericValue);

            /*
             * |    A(10)
             * |  /   \
             * | B(11)  C(12)
             * |      /   \
             * |     D(13) E(14)
             * |    /  \
             * |  F(15) G(16)
             */
        assertThat(exported).containsExactlyInAnyOrder(
                TreeElement.buildFromRootElement('A'),
                TreeElement.buildFromElementAndParentId('B', 10),
                TreeElement.buildFromElementAndParentId('C', 10),
                TreeElement.buildFromElementAndParentId('D', 12),
                TreeElement.buildFromElementAndParentId('E', 12),
                TreeElement.buildFromElementAndParentId('F', 13),
                TreeElement.buildFromElementAndParentId('G', 13)
        );


        Tree<Character> imported = TreeJsonImporter.importTree(exported, Character::getNumericValue);

        verifyTreeNode(imported, 'A', new Character[]{'B', 'C'});
        verifyTreeNode(imported, 'B', new Character[]{});
        verifyTreeNode(imported, 'C', new Character[]{'D', 'E'});
        verifyTreeNode(imported, 'D', new Character[]{'F', 'G'});
        verifyTreeNode(imported, 'E', new Character[]{});
        verifyTreeNode(imported, 'F', new Character[]{});
        verifyTreeNode(imported, 'G', new Character[]{});

    }

    protected <T> void verifyTreeNode(Tree<T> tree, T nodeData, T... expectedElements) {
        tree.lookup(nodeData)
                .map(node -> assertThat(node.getChildren()).containsOnly(expectedElements))
                .orElseThrow(() -> new RuntimeException(String.format("Node with data '%s' was not found", nodeData)));
    }

}
