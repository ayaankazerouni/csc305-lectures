package main;

import java.util.List;

import static main.CoverageDemo.TRIANGLE_TYPE.*;

public class CoverageDemo {

    public static TRIANGLE_TYPE getTriangleType(int side1, int side2, int side3) {
        if (side1 <= 0 || side2 <= 0 || side3 <= 0) {
            throw new IllegalArgumentException("Side lengths must be greater than 0.");
        }

        if (side1 >= side2 + side3 || side2 >= side1 + side3 || side3 >= side1 + side2) {
            return INVALID;
        }

        if (side1 == side2 && side2 == side3) {
            return EQUILATERAL;
        }

        if (side1 == side2 || side2 == side3 || side1 == side3) {
            return ISOSCELES;
        }

        return SCALENE;
    }

    public static int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Given a list of integers representing daily inches of rainfall,
     * count the average rainfall over the given period. There are some
     * erroneous measurements that came in as negative numbers, so ignore
     * those. Stop counting when you see the sentinel number (999).
     *
     * @param rainfall Daily inches of rainfall
     */
    public static double rainfall(List<Integer> rainfall) {
        double total = 0;
        int days = 0;

        for (int current : rainfall) {
            if (current == 999) {
                break;
            }

            if (current >= 0) {
                total = total + current;
                days = days + 1;
            }
        }

        return days == 0 ? 0 : total / days;
    }

    public enum TRIANGLE_TYPE {
        EQUILATERAL,
        ISOSCELES,
        SCALENE,
        INVALID
    }
}
