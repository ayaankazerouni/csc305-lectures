package main.classic;

public class Museum implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
