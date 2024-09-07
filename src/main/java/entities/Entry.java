package entities;

public class Entry <E, T>{
    private final E key;
    private final T value;

    public Entry(E key, T value) {
        this.key = key;
        this.value = value;
    }

    public E getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}
