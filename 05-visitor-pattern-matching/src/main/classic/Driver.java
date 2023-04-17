package main.classic;

import java.util.List;

public class Driver {
    public static void main(String[] args) {
        List<Element> elements = List.of(
                new Landmark(),
                new SightseeingArea(),
                new Landmark(),
                new Museum()
        );


        XMLExportVisitor visitor = new XMLExportVisitor();

        for (Element current : elements) {
            current.accept(visitor);
        }
    }
}
