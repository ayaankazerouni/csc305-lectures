package main.classic;

public interface Visitor {
    void visit(SightseeingArea s);
    void visit(Landmark l);
    void visit(Museum m);
}
