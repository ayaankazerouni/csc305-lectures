package main.modern;

import java.util.List;

public class Driver {
    public static void main(String[] args) {
        List<Element> elements = List.of(
                new Landmark(),
                new SightseeingArea(),
                new Landmark(),
                new Museum()
        );

        exportXML(elements);
    }


    // This type of "pattern matching" is experimental and is coming
    // soon to a JDK near you. (SonarLint has no idea how to read this code,
    // so it thinks my indentation is broken.)
    public static void exportXML(List<Element> elements) {
        for (Element current : elements) {
            switch (current) {
                case SightseeingArea s -> System.out.println("Visiting sightseeing area");
                case Landmark l -> System.out.println("Visiting landmark");
                case Museum m -> System.out.println("Visiting museum");
                // If these cases are not exhaustive, code won't compile.
            }
        }
    }
}
