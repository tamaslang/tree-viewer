package org.talangsoft.tree.exportimport.treeelement;


import org.talangsoft.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeFromTreeElementBuilder {
    public static <T, ID, E extends ToParentReferringTreeElement<ID, T>> Tree<T> importTree(List<E> treeElements, Function<T, ID> idForElementProvider) {

        Optional<E> rootElement = treeElements.stream().filter(element -> !element.getParentId().isPresent()).findFirst();


        return rootElement.map(root -> findAndRemoveChildrenOfTheNodeAndInsertThemAsSubNodes(new Tree<T>(root.getElement()), new ArrayList<>(treeElements), idForElementProvider)).orElseThrow(
                () -> new RuntimeException("Root element is not present in between the exportedElements")
        );
    }

    protected static <T, ID, E extends ToParentReferringTreeElement<ID, T>> Tree<T> findAndRemoveChildrenOfTheNodeAndInsertThemAsSubNodes(Tree<T> me, List<E> treeElements, Function<T, ID> idForElementProvider) {
        // find children
        List<E> myChildren = treeElements.stream()
                .filter(elementWithParentRef -> elementWithParentRef.getParentId().isPresent())
                .filter(elementWithParentRef -> elementWithParentRef.getParentId().get().equals(idForElementProvider.apply(me.getData())))
                .collect(Collectors.toList());

        // remove from exported elements
        treeElements.remove(myChildren);
        myChildren.forEach(myChild -> me.insert(myChild.getElement()));

        // call the same with all my children recursively
        me.getChildNodes().forEach(childNode -> findAndRemoveChildrenOfTheNodeAndInsertThemAsSubNodes(childNode, treeElements, idForElementProvider));
        return me;
    }
}
