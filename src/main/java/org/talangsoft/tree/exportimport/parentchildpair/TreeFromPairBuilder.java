package org.talangsoft.tree.exportimport.parentchildpair;

import org.talangsoft.tree.Tree;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeFromPairBuilder {
    protected static <T> Tree<T> addPair(Tree<T> tree, ParentChildPair<T> pair) {
        return tree.lookup(pair.getParent())
                .map(parentElement -> {
                    // lookup child
                    Optional<Tree<T>> childElement = tree.lookup(pair.getChild());
                    // if it does not exists
                    Tree<T> childToInsert = childElement.orElseGet(() -> new Tree(pair.getChild()));
                    // remove from old parent
                    childToInsert.getParent().map(parent -> parent.getChildNodes().remove(childToInsert));
                    // insert to this parent
                    return parentElement.insert(childToInsert);
                })
                .orElseThrow(() -> new RuntimeException(String.format("Parent '%s' does not exist in tree", pair.getParent()))
                );
    }

    /**
     * Build tree from repeated pair, a parent and it's child on any level will be a pair in the list
     * The tree like:
     * |    A
     * |  /   \
     * | B     C
     * |      / \
     * |     D   E
     * |    / \
     * |   F  G
     * <p>
     * is represented with the following pairs:
     * A-B; A-C; A-D; A-F; A-G; A-E; C-D; C-F; C-G; C-E; D-F; D-G
     */
    public static <T> Tree<T> buildFromRepeatedParentChildPairs(List<ParentChildPair<T>> pairs) {
        // pairs need to be sorted based on occurences of the parent node
        List<ParentChildPair<T>> sortedPairs = getSortedParentChildPairs(pairs);

        Tree<T> root = new Tree<T>(sortedPairs.get(0).getParent());
        sortedPairs.stream().forEach(pair -> addPair(root, pair));
        return root;
    }


    /**
     * Build tree from repeated pair, a parent and it's child on any level will be a pair in the list
     * The tree like:
     * |    A
     * |  /   \
     * | B     C
     * |      / \
     * |     D   E
     * |    / \
     * |   F  G
     * <p>
     * is represented with the following pairs:
     * A-B; A-C; C-D; C-F; C-E; D-F; D-G
     */
    public static <T> Tree<T> buildFromParentChildPairs(List<ParentChildPair<T>> pairs) {
        Set<Tree<T>> parentChildTrees = pairs.stream().map(pair -> new Tree<T>(pair.getParent(), pair.getChild())).collect(Collectors.toSet());

        Tree<T> root = parentChildTrees.stream().findFirst().get();
        parentChildTrees.remove(root);
        while (!parentChildTrees.isEmpty()) {
            final Tree<T> treeRoot = root;
            List<Tree<T>> elementsCanBeInserted = parentChildTrees.stream().filter(
                    element -> treeRoot.getData().equals(element.getFirstChild().get()) || treeRoot.lookup(element.getData()).isPresent()
            ).collect(Collectors.toList());
            // remove from set
            parentChildTrees.removeAll(elementsCanBeInserted);
            // insert elements
            for (Tree<T> elementToInsert : elementsCanBeInserted) {
                if (elementToInsert.getFirstChild().get().equals(root.getData())) {
                    elementToInsert.getChildNodes().clear();
                    elementToInsert.insert(root);
                    root = elementToInsert;
                } else {
                    Tree<T> newParent = root.lookup(elementToInsert.getData()).get();
                    newParent.insert(elementToInsert.getFirstChild().get());
                }
            }
        }

        return root;
    }

    private static <T> List<ParentChildPair<T>> getSortedParentChildPairs(List<ParentChildPair<T>> pairs) {
        Map<T, Long> parentsByOccurences =
                pairs.stream().map(ParentChildPair::getParent)
                        .collect(Collectors.groupingBy(p1 -> p1, Collectors.counting()));


        return pairs.stream()
                .sorted((p1, p2) -> getWeightOf(parentsByOccurences, p2) - getWeightOf(parentsByOccurences, p1))
                .collect(Collectors.toList());
    }

    private static <T> int getWeightOf(Map<T, Long> parentsByOccurences, ParentChildPair<T> element) {
        return parentsByOccurences.getOrDefault(element.getParent(), 0l).intValue();
    }

}
