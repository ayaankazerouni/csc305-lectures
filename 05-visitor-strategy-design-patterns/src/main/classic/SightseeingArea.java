package main.classic;

public class SightseeingArea implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
