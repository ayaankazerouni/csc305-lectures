package main.classic;

public class XMLExportVisitor implements Visitor {
    @Override
    public void visit(SightseeingArea s) {
        System.out.println("Visiting a sightseeing area");
    }

    @Override
    public void visit(Landmark l) {
        System.out.println("Visiting a landmark");
    }

    @Override
    public void visit(Museum m) {
        System.out.println("Visiting a museum");
    }
}
