package main;

import java.util.Deque;
import java.util.ArrayDeque;

/**
 * This class represents a Parser which can create an Expression from a given input String.
 * It can parse prefix notation arithmetic expressions, containing the operators +, -, *, /, %, ^, and digits.
 */
public class Parser {

    private Parser() {
        throw new IllegalStateException();
    }

    /**
     * Parses a prefix arithmetic expression and returns an {@link Expression} object.
     *
     * @param input the input string containing the prefix expression
     * @return the {@link Expression} object representing the expression
     * @throws IllegalArgumentException if the input string is not a valid prefix expression
     */
    public static Expression prefixParse(String input){
        String[] tokens = input.split("\\s+");
        Deque<Expression> expressions = new ArrayDeque<>();
        for(int i = tokens.length - 1; i >= 0; i--){
            String token = tokens[i];
            if (isInteger(token)) {
                expressions.push(new Digit(Integer.parseInt(token)));
            } else if (isOperator(token)) {
                if (expressions.size() < 2){
                    throw new IllegalArgumentException("Not enough numbers for a operation");
                }
                Expression firstExpression = expressions.pop();
                Expression secondExpression = expressions.pop();
                expressions.push(createOperation(token, firstExpression, secondExpression));
            } else {
                throw new IllegalArgumentException("Not a valid Operator or Number");
            }
        }
        if (expressions.size() != 1){
            throw new IllegalArgumentException("Not the correct amount of Operators or Numbers");
        }
        return expressions.pop();
    }

    private static Operation createOperation(String token, Expression leftOperand, Expression rightOperand) {
        return switch (token) {
            case "+" -> new AddOperator(leftOperand, rightOperand);
            case "-" -> new SubtractOperator(leftOperand, rightOperand);
            case "*" -> new MultiplyOperator(leftOperand, rightOperand);
            case "/" -> new DivideOperator(leftOperand, rightOperand);
            case "^" -> new ExponentOperator(leftOperand, rightOperand);
            case "%" -> new ModuloOperator(leftOperand, rightOperand);
            default -> throw new IllegalArgumentException("Invalid operator: " + token);
        };
    }

    private static boolean isOperator(String token) {
        return token.equals("+") ||
                token.equals("-") ||
                token.equals("*") ||
                token.equals("/") ||
                token.equals("^") ||
                token.equals("%");
    }

    private static boolean isInteger(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
