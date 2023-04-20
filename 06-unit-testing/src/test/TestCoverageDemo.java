package test;

import static main.CoverageDemo.TRIANGLE_TYPE.EQUILATERAL;
import static main.CoverageDemo.getTriangleType;
import static main.CoverageDemo.multiply;
import static org.junit.jupiter.api.Assertions.*;

import main.CoverageDemo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TestCoverageDemo {

    @Test
    public void testEquilateral() {
        assertThat(multiply(2, 2)).isEqualTo(4);
    }

}
