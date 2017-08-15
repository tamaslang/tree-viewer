package org.talangsoft.tree.exportimport.parentchildpair;

public class ParentChildPair<T> {
    private T parent;
    private T child;

    public ParentChildPair(T parent, T child) {
        this.parent = parent;
        this.child = child;
    }

    public T getParent() {
        return parent;
    }

    public T getChild() {
        return child;
    }

    @Override
    public String toString() {
        return "ParentChildPair{" +
                "parent=" + parent +
                ", child=" + child +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParentChildPair<?> that = (ParentChildPair<?>) o;

        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        return child != null ? child.equals(that.child) : that.child == null;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (child != null ? child.hashCode() : 0);
        return result;
    }
}
