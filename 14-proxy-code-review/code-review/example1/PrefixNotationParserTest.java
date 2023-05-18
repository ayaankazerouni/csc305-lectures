package main.test;

import main.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrefixNotationParserTest {

    private static final String INVALID_INPUT = "Invalid input: ";
    private static final String PARSE_ERROR = "parse given empty list.";

    @Test
    public void singleIntegerNumberParseTest() {
        Assertions.assertEquals(
                new NumberExpression(5),
                PrefixNotationParser.parse("5"));
    }

    @Test
    public void singleDecimalNumberParseTest() {
        Assertions.assertEquals(
                new NumberExpression(5.3),
                PrefixNotationParser.parse("5.3"));
    }

    @Test
    public void binOpAddParseTest() {
        AdditionBinaryOperatorExpression add = new AdditionBinaryOperatorExpression(
                new NumberExpression(2),
                new NumberExpression(3));

        Assertions.assertEquals(add, PrefixNotationParser.parse("+ 2 3"));
    }

    @Test
    public void binOpExponentParseTest() {
        ExponentiationBinaryOperatorExpression exponent = new ExponentiationBinaryOperatorExpression(
                new NumberExpression(2),
                new NumberExpression(3)
        );

        Assertions.assertEquals(exponent, PrefixNotationParser.parse("^ 2 3"));
    }

    @Test
    public void binOpSubParseTest() {
        SubtractionBinaryOperatorExpression add = new SubtractionBinaryOperatorExpression(
                new NumberExpression(2),
                new NumberExpression(3));

        Assertions.assertEquals(add, PrefixNotationParser.parse("- 2 3"));
    }

    @Test
    public void binOpMultParseTest() {
        MultiplicationBinaryOperatorExpression mult = new MultiplicationBinaryOperatorExpression(
                new NumberExpression(2),
                new NumberExpression(3)
        );

        Assertions.assertEquals(mult, PrefixNotationParser.parse("* 2 3"));
    }


    @Test
    public void compoundBinOpParseTest() {
        AdditionBinaryOperatorExpression exp = new AdditionBinaryOperatorExpression(
                new MultiplicationBinaryOperatorExpression(
                        new NumberExpression(3),
                        new NumberExpression(6)),
                new ModuloBinaryOperatorExpression(
                        new NumberExpression(13),
                        new NumberExpression(2)));

        Assertions.assertEquals(exp, PrefixNotationParser.parse("+ * 3 6 % 13 2"));
    }

    @Test
    public void badInputEmptyString() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->
                PrefixNotationParser.parse(""));

        Assertions.assertTrue(e.getMessage().contains(PARSE_ERROR));
    }

    @Test
    public void badInputNotEnoughNumbersTest1() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->
            PrefixNotationParser.parse("+ 1"));

        Assertions.assertTrue(e.getMessage().contains(PARSE_ERROR));
    }

    @Test
    public void badInputNotEnoughNumbersTest2() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->
            PrefixNotationParser.parse("+ * 2 / 3 5"));

        Assertions.assertTrue(e.getMessage().contains(PARSE_ERROR));
    }

    @Test
    public void badInputNotEnoughNumbersTest3() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->
            PrefixNotationParser.parse("+ * 2 3 / 3"));

        Assertions.assertTrue(e.getMessage().contains(PARSE_ERROR));
    }

    @Test
    public void badInputUnrecognizedSymbolTest() {
        String badSymbol = "orange";
        Exception e = Assertions.assertThrows(NumberFormatException.class, () ->
            PrefixNotationParser.parse("* 1 " + badSymbol));

        String expectedMessage = INVALID_INPUT + badSymbol;
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    @Test
    public void badInputUnrecognizedSymbolTestNonsense() {
        Exception e = Assertions.assertThrows(NumberFormatException.class, () ->
                PrefixNotationParser.parse("a8a3 9a83 9a83fnpa f"));

        Assertions.assertTrue(e.getMessage().contains(INVALID_INPUT));
    }

    @Test
    public void badInputContainsSymbolsAfterInitialNumber1() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->
                PrefixNotationParser.parse("5 + 1 2"));

        String expectedMessage = INVALID_INPUT + "5 + 1 2";
        Assertions.assertTrue(e.getMessage().contains(expectedMessage));
    }

    @Test
    public void badInputContainsSymbolsAfterInitialNumber2() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->
                PrefixNotationParser.parse("5 10 14 1234"));

        Assertions.assertTrue(e.getMessage().contains(INVALID_INPUT));
    }





}
