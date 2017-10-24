package org.talangsoft.tree.exportimport.treeelement;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode(exclude = "element")
public class TreeElement<ID, T> implements ToParentReferringTreeElement {

    @JsonUnwrapped
    private T element;
    private Optional<ID> parentId;

    public TreeElement(T element, Optional<ID> parentId) {
        this.element = element;
        this.parentId = parentId;
    }

    public TreeElement() {
    }

    public static <ID, T> TreeElement<ID, T> buildFromElementAndParentId(T element, ID parentId) {
        return buildFromElementAndParentOption(element, Optional.of(parentId));
    }

    public static <ID, T> TreeElement<ID, T> buildFromRootElement(T element) {
        return buildFromElementAndParentOption(element, Optional.empty());
    }

    public static <ID, T> TreeElement<ID, T> buildFromElementAndParentOption(T element, Optional<ID> parentId) {
        return new TreeElement(element, parentId);
    }

    @Override
    public T getElement() {
        return element;
    }

    @Override
    public Optional<ID> getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "TreeElement{" +
                "element=" + element +
                ", parentId=" + parentId +
                '}';
    }
}
