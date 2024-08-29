package database;

public interface AbstractBatchRepository<E> extends AbstractRepository<E> {
    void addToBatch(E element);

    void executeBatch();

    void flushBatch();

}
