package main;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The parser takes in text and validates prefix expressions as well as turns them into queues and expression trees to
 * be later evaluated
 */
public class Parser {

    /**
     * Acts as a way to take user input and check to see if it is a valid prefix expression
     * @param str Array of Strings to be validated
     * @param size size of the inputted array
     * @return true if the array expresses a valid prefix expression false otherwise
     */
    public boolean isValidPrefix(String[] str, int size){

        if(str == null) {
            return false;
        }

        int numOperator = 0;
        int numOperand = 0;

        for(int i = 0; i < size; i++){
            if(!isOperator(str[i]) && !isDouble(str[i]))
                return false;

            if(isOperator(str[i])) {
                numOperator++;
            } else{
                numOperand++;
            }

            // this is a check to see if any prefix without the last two operands, shows that the number of operators is more
            // than or equal to the number of operands
            if (i < (size - 2) && numOperator < numOperand){
                return false;
            }

        }

        return numOperand == numOperator + 1;
    }

    /**
     * @param input an array of strings that should resemble a prefix expression
     * @return Queue of strings to be later transformed into an expression tree
     */
    public Queue<String> formInputQueue(String[] input){
        Queue<String> queue = new LinkedList<>();

        if (input == null)
            return queue;

        for (String s : input) {
            if (isOperator(s) || isDouble(s))
                queue.add(s);
            else
                throw new IllegalArgumentException("Input List was not a valid prefix expression");
        }
        return queue;
    }

    /**
     * @param q a queue resembling a prefix expression
     * @return an expression tree that resembles the inputted prefix expression
     * */
    public Expression createExpressionTree(Queue<String> q){
        if(q.isEmpty())
            return null;

        String cur = q.remove();

        if(isDouble(cur)){
            return new Digit(Double.parseDouble(cur));
        } else if(isOperator(cur)){
            Operation e = strToOperation(cur);
            e.setLeft(createExpressionTree(q));
            e.setRight(createExpressionTree(q));
            return e;
        } else{
            throw new IllegalArgumentException("Queue does not contain a valid prefix expression");
        }

    }

    /**
     * @param str string resembling an operator
     * @return the operation that is resembled by the string operator
     */
    private Operation strToOperation(String str){
        return switch (str) {
            case "+" -> new PlusOp();
            case "-" -> new MinusOp();
            case "*" -> new MultOp();
            case "/" -> new DivideOp();
            case "%" -> new ModOp();
            default -> new ExpOp();
        };

    }

    /**
     * @param str String that may be a double
     * @return true if an inputted string is a double, false otherwise
     */
    public boolean isDouble(String str){
        if(str == null)
            return false;

        try{
            Double.parseDouble(str);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    /**
     * @param str String that may be an operator
     * @return true if an inputted string is an operator, false otherwise
     */
    public boolean isOperator(String str){
        return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/")
                || str.equals("%") || str.equals("^");
    }

    /**
     * @param str string to be interpretted as a possible input for a history check
     * @return true if the string is in valid format to be checked in history false otherwise
     */
    public boolean isHistCommand(String str){
        return !isDouble(str) && !isOperator(str) && str.length() == 1;
    }
}
