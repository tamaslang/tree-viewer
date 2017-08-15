package org.talangsoft.tree.printer;

import org.talangsoft.tree.Tree;

import java.util.stream.Collectors;

public class TreePrinter {
    public static <T> void print(Tree<T> tree) {
        String childrenString = tree.getChildren().stream().map(Object::toString).collect(Collectors.joining(","));
        System.out.println(String.format("TreeNode[%s -> {%s}]", tree.getData().toString(), childrenString));
        tree.getChildNodes().stream().forEach(TreePrinter::print);
    }
}
