package org.trinity.util.convert;

import java.math.BigDecimal;

public class BigDecimalUtil {
    public static double subtract(double a, double b) {
        return BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b)).doubleValue();
    }

    public static double add(double a, double b) {
        return BigDecimal.valueOf(a).add(BigDecimal.valueOf(b)).doubleValue();
    }
}
