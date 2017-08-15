package org.talangsoft.tree.exportimport.treeelement;

import org.talangsoft.tree.Tree;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeToTreeElementExporter {
    public static <T, ID, E extends ToParentReferringTreeElement<ID, T>> List<E> exportTree(Tree<T> tree, Function<T, ID> idForElementProvider, BiFunction<T, Optional<ID>, E> treeElementCreator) {
        List<E> exportElements = tree.allNodes().stream()
                .map(node -> treeElementCreator.apply(node.getData(), node.getParentElement().map(idForElementProvider)))
                .collect(Collectors.toList());
        return exportElements;
    }
}


