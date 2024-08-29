package database;

public interface AbstractRepository<E> {
    E getByName(String name);

    void selectAll();

    void save(E element);
}
