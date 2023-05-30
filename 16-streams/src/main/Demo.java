package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Demo {
    public static void main(String[] args) throws IOException {
        String secretPhrase = "secret phrase";

//        try (Stream<String> stream = Files.lines(Path.of("file.txt"))) {
//            OptionalInt result = stream.parallel().map(s -> {
//                System.out.println("UPCASING");
//                return s.toUpperCase();
//            }).filter(s -> {
//                System.out.println("\tCHECKING FOR SECRET PHRASE");
//                return s.contains(secretPhrase.toUpperCase());
//            }).mapToInt(s -> {
//                System.out.println("\t\tMAPPING TO LENGTH");
//                return s.length();
//            }).max();
//        }
    }
}
