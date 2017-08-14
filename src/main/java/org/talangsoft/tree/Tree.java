package org.talangsoft.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Tree<T> {
    private T data;
    private Optional<Tree<T>> parent;
    private List<Tree<T>> childNodes = new ArrayList<>();


    public Tree(T data, Tree<T> parent, List<Tree<T>> childNodes) {
        this.data = data;
        this.parent = Optional.of(parent);
        this.childNodes.addAll(childNodes);
    }

    public Tree(T data, List<Tree<T>> childNodes) {
        this.data = data;
        this.parent = Optional.empty();
        this.childNodes.addAll(childNodes);
    }

    public Tree(T data) {
        this.data = data;
        this.parent = Optional.empty();
    }

    public Optional<Tree<T>> lookup(T elem) {
        if (this.getData().equals(elem)) return Optional.of(this);
        for (Tree<T> child : childNodes) {
            Optional<Tree<T>> search = child.lookup(elem);
            if (search.isPresent()) return search;
        }
        return Optional.empty();
    }

    public List<T> allBottomLevelSuccessor() {
        if (childNodes.isEmpty()) {
            return Collections.singletonList(this.data);
        }
        List<T> successors = new ArrayList<>();
        for (Tree<T> child : childNodes) {
            successors.addAll(child.allBottomLevelSuccessor());
        }
        return successors;
    }

    public List<T> allElements() {
        List<T> elements = new ArrayList<>();
        elements.add(this.data);
        for (Tree<T> child : childNodes) {
            elements.addAll(child.allElements());
        }
        return elements;
    }

    public List<Tree<T>> allNodes() {
        List<Tree<T>> elements = new ArrayList<>();
        elements.add(this);
        for (Tree<T> child : childNodes) {
            elements.addAll(child.allNodes());
        }
        return elements;
    }

    public Tree<T> insert(T child) {
        childNodes.add(new Tree(child, this, Collections.emptyList()));
        return this;
    }

    public Tree<T> insert(Tree<T> child) {
        child.parent = Optional.of(this);
        childNodes.add(child);
        return this;
    }

    public T getData() {
        return data;
    }

    public Optional<Tree<T>> getParent() {
        return parent;
    }

    public Optional<T> getParentElement() {
        return parent.map(parent -> parent.getData());
    }

    public List<T> getChildren() {
        return childNodes.stream().map(Tree::getData).collect(Collectors.toList());
    }

    public List<Tree<T>> getChildNodes() {
        return childNodes;
    }

}