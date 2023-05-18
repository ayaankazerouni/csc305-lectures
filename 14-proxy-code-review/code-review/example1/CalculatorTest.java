import main.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

class CalculatorTest {

  @Test
  void testCalculate() {
    String expectedResult = "^ 5 2 => 25";
    Calculator.setHistoryLength(3);
    String actualResult = Calculator.calculate("^ 5 2");

    Assertions.assertEquals(expectedResult, actualResult);

    String expectedResult2 = "* 5 2 => 10";
    Calculator.setHistoryLength(3);
    String actualResult2 = Calculator.calculate("* 5 2");

    Assertions.assertEquals(expectedResult2, actualResult2);

    String expectedResult3 = "/ 5 2 => 2.5";
    String actualResult3 = Calculator.calculate("/ 5 2");

    Assertions.assertEquals(expectedResult3, actualResult3);

    String expectedResult4 = "% 5 2 => 1";
    String actualResult4 = Calculator.calculate("% 5 2");

    Assertions.assertEquals(expectedResult4, actualResult4);

    String expectedResult5 = "+ 5 2 => 7";
    String actualResult5 = Calculator.calculate("+ 5 2");

    Assertions.assertEquals(expectedResult5, actualResult5);

    String expectedResult6 = "- 5 2 => 3";
    String actualResult6 = Calculator.calculate("- 5 2");

    Assertions.assertEquals(expectedResult6, actualResult6);

    String expectedResult7 = "3 => 3";
    String actualResult7 = Calculator.calculate("3");

    Assertions.assertEquals(expectedResult7, actualResult7);
  }

  @Test
  void testCalculateInvalidInputs() {
    String expectedMessage = "Wrong format! Please use prefix notation!";

    InputMismatchException thrown = Assertions.assertThrows(InputMismatchException.class,
        ()-> Calculator.calculate("n")
    );

    Assertions.assertEquals(thrown.getMessage(), expectedMessage);

    InputMismatchException thrown2 = Assertions.assertThrows(InputMismatchException.class,
        ()-> Calculator.calculate("/ 5 0")
    );

    Assertions.assertEquals(thrown2.getMessage(), expectedMessage);

    InputMismatchException thrown3 = Assertions.assertThrows(InputMismatchException.class,
        ()-> Calculator.calculate("8 / 6")
    );

    Assertions.assertEquals(thrown3.getMessage(), expectedMessage);

    InputMismatchException thrown4 = Assertions.assertThrows(InputMismatchException.class,
        ()-> Calculator.calculate("/ 4")
    );

    Assertions.assertEquals(thrown4.getMessage(), expectedMessage);

    InputMismatchException thrown5 = Assertions.assertThrows(InputMismatchException.class,
        ()-> Calculator.calculate("- 5 7 3")
    );

    Assertions.assertEquals(thrown5.getMessage(), expectedMessage);
  }

  @Test
  void testCalculateCompoundExpressions() {
    String expectedResult = "+ 3 / * 12 0.5 2 => 6";
    String actualResult = Calculator.calculate("+ 3 / * 12 0.5 2");

    Assertions.assertEquals(expectedResult, actualResult);

    String expectedResult2 = "- 8 % 5 4 => 7";
    String actualResult2 = Calculator.calculate("- 8 % 5 4");

    Assertions.assertEquals(expectedResult2, actualResult2);

    String expectedResult3 = "+ / ^ 19 2 4 35 => 125.25";
    String actualResult3 = Calculator.calculate("+ / ^ 19 2 4 35");

    Assertions.assertEquals(expectedResult3, actualResult3);
  }

  @Test
  void testCalculateInvalidCharacter() {
    String expectedMessage = "Not a valid operator or number!";

    InputMismatchException thrown = Assertions.assertThrows(InputMismatchException.class,
        ()-> Calculator.calculate("- n 7")
    );

    Assertions.assertEquals(thrown.getMessage(), expectedMessage);
  }

  @Test
  void testGetExpressionHistory() {
    Calculator.setHistoryLength(3);
    Calculator.calculate("/ 10 5");
    Calculator.calculate("+ 5 3");
    Calculator.calculate("* 9 1");
    List<String> expectedHistory = new ArrayList<>();
    expectedHistory.add("* 9 1 => 9");
    expectedHistory.add("+ 5 3 => 8");
    expectedHistory.add("/ 10 5 => 2");
    List<String> actualHistory = Calculator.getExpressionHistory();

    Assertions.assertEquals(expectedHistory, actualHistory);
  }

  @Test
  void testUpdateExpressionHistory() {
    Calculator.setHistoryLength(3);
    Calculator.calculate("/ 10 5");
    Calculator.calculate("+ 5 3");
    Calculator.calculate("* 9 1");
    Calculator.updateExpressionHistory(2);
    List<String> expectedHistory = new ArrayList<>();
    expectedHistory.add("/ 10 5 => 2");
    expectedHistory.add("* 9 1 => 9");
    expectedHistory.add("+ 5 3 => 8");
    List<String> actualHistory = Calculator.getExpressionHistory();

    Assertions.assertEquals(expectedHistory, actualHistory);
  }

  @Test
  void testSetHistoryLength() {
    int expectedHistoryLength = 3;
    int actualHistoryLength = Calculator.getHistoryLength();

    Assertions.assertEquals(expectedHistoryLength, actualHistoryLength);
  }
}
