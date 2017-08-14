package org.talangsoft.tree;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public static <T> Tree<T> build(List<ParentChildPair<T>> pairs) {
        // pairs need to be sorted based on occurences of the parent node
        List<ParentChildPair<T>> sortedPairs = getSortedParentChildPairs(pairs);

        Tree<T> root = new Tree<T>(sortedPairs.get(0).getParent());
        sortedPairs.stream().forEach(pair -> addPair(root, pair));
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
