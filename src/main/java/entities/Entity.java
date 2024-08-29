package entities;

public class Entity {
    private static int counter = 0;

    private int id;

    public Entity() {
        this.id = ++counter;
    }

    public int getId() {
        return id;
    }
}
