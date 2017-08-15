package org.talangsoft.tree.exportimport.treeelement;

import java.util.Optional;

public interface ToParentReferringTreeElement<ID, T> {

    T getElement();

    Optional<ID> getParentId();
}
