package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BigNumberCalculator {

    /**
     * Add two positive integers represented by lists of digits.
     *
     * @param left The left operand
     * @param right The right operand
     * @return A list of integers representing the sum of left and right
     * @throws IllegalArgumentException if any digits in either list is < 0 or > 9
     */
    public static List<Integer> plus(List<Integer> left, List<Integer> right) {
        if (left == null || right == null)  {
            return null;
        }

        left = new ArrayList<>(left);
        Collections.reverse(left);
        right = new ArrayList<>(right);
        Collections.reverse(right);

        LinkedList<Integer> result = new LinkedList<>();

        int carry = 0;
        for (int i = 0; i < Math.max(left.size(), right.size()); i++) {
            int leftDigit = left.size() > i ? left.get(i) : 0;
            int rightDigit = right.size() > i ? right.get(i) : 0;

            // Throw an exception if the precondition doesn't hold
            if (leftDigit < 0 || leftDigit > 10 || rightDigit < 0 || rightDigit > 9) {
                throw new IllegalArgumentException();
            }

            int sum = leftDigit + rightDigit + carry;

            result.addFirst(sum % 10);
            carry = sum / 10;
        }

        return result;
    }
}
