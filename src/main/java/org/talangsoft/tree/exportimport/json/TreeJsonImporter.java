package org.talangsoft.tree.exportimport.json;


import org.talangsoft.tree.Tree;
import org.talangsoft.tree.exportimport.TreeElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeJsonImporter {
    public static <ID, T> Tree<T> importTree(List<TreeElement<ID, T>> exportedElements, Function<T, ID> idForElementProvider) {

        Optional<TreeElement<ID, T>> rootElement = exportedElements.stream().filter(element -> !element.getParentId().isPresent()).findFirst();


        return rootElement.map(root -> findAndRemoveChildrenOfTheNodeAndInsertThemAsSubNodes(new Tree<T>(root.getElement()), new ArrayList<>(exportedElements), idForElementProvider)).orElseThrow(
                () -> new RuntimeException("Root element is not present in between the exportedElements")
        );
    }

    protected static <ID, T> Tree<T> findAndRemoveChildrenOfTheNodeAndInsertThemAsSubNodes(Tree<T> me, List<TreeElement<ID, T>> exportedElements, Function<T, ID> idForElementProvider) {
        // find children
        List<TreeElement<ID, T>> myChildren = exportedElements.stream()
                .filter(elementWithParentRef -> elementWithParentRef.getParentId().isPresent())
                .filter(elementWithParentRef -> elementWithParentRef.getParentId().get().equals(idForElementProvider.apply(me.getData())))
                .collect(Collectors.toList());

        // remove from exported elements
        exportedElements.remove(myChildren);
        myChildren.forEach(myChild -> me.insert(myChild.getElement()));

        // call the same with all my children recursively
        me.getChildNodes().forEach(childNode -> findAndRemoveChildrenOfTheNodeAndInsertThemAsSubNodes(childNode, exportedElements, idForElementProvider));
        return me;
    }
}
