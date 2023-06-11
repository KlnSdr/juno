package interpreter;

public class JunoTuple<X, Y> {
    private final X x;
    private final Y y;

    public JunoTuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X _1() {
        return x;
    }

    public Y _2() {
        return y;
    }
}
