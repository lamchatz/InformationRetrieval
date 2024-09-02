package dto;

public class Speech {
    private final String text;

    public Speech(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Speech{").append("\n");
        sb.append("text='").append(text).append('\'').append("\n");
        sb.append('}');
        return sb.toString();
    }
}
