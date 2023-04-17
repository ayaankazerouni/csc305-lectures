package main.classic;

public class Landmark implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
