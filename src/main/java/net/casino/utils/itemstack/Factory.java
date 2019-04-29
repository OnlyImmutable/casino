package net.casino.utils.itemstack;

public abstract class Factory<T> {

    /** Object being put through the 'factory'. */
    protected T object;

    /** Called once everything has been set during creation. */
    public abstract T build();
}
