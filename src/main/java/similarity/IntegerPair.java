package similarity;

public class IntegerPair {

    private Integer id1;
    private Integer id2;

    public IntegerPair(Integer id1, Integer id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    public Integer getId1() {
        return id1;
    }

    public Integer getId2() {
        return id2;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IntegerPair{");
        sb.append("id1=").append(id1);
        sb.append(", id2=").append(id2);
        sb.append('}');
        return sb.toString();
    }
}
