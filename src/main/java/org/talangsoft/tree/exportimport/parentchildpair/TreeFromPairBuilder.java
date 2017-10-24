package org.talangsoft.tree.exportimport.parentchildpair;

import org.talangsoft.tree.Tree;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.*;
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
        Set<Tree<T>> nodes = new HashSet<>();

        for (ParentChildPair<T> pair : pairs) {
            Tree<T> parent = nodeForData(pair.getParent(), nodes);
            Tree<T> child = nodeForData(pair.getChild(), nodes);

            parent.insert(child);
        }

        return nodes.stream().filter(tree -> !tree.getParent().isPresent()).findFirst().get();
    }

    private static <T> Tree<T> nodeForData(T data, Set<Tree<T>> nodes) {
        // return existing node (Tree) whose data equals T, otherwise create and return new Tree after adding to Set of nodes
        return nodes.stream().filter(node -> node.getData().equals(data)).findFirst().orElseGet(() -> {
            Tree<T> newTree = new Tree<T>(data);
            nodes.add(newTree);
            return newTree;
        });
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
