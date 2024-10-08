package dto;

public class Speech {
    private final int id;
    private final String content;

    public Speech(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "{ " + id + ", " + content + " }";
    }
}
