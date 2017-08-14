package org.talangsoft.tree.exportimport.json;


import org.talangsoft.tree.Tree;
import org.talangsoft.tree.exportimport.TreeElement;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeJsonExporter {
    public static <ID, T> List<TreeElement<ID, T>> exportTree(Tree<T> tree, Function<T, ID> idForElementProvider) {
        List<TreeElement<ID, T>> exportElements = tree.allNodes().stream()
                .map(node ->
                        TreeElement.<ID, T>buildFromElementAndParentOption(
                                node.getData(),
                                node.getParentElement().map(idForElementProvider)
                        ))
                .collect(Collectors.toList());
        return exportElements;
    }
}


