package fma;

public class Pair<A, B> {
    private A a;
    private B b;

    public Pair(A first, B second) {
        super();
        this.a = first;
        this.b = second;
    }


    public A getFirst() {
        return a;
    }

    public void setFirst(A a) {
        this.a = a;
    }

    public B getSecond() {
        return b;
    }

    public void setSecond(B b) {
        this.b = b;
    }
}
