package org.trinity.wallet;

import org.junit.Test;
import org.trinity.util.algorithm.Base58Util;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testBase58() {
        byte[] decode = Base58Util.decode("47o5PTwgSarBqiiBHoRQWL7QZGZ1zq199FBAU52kndkLxt3f2enAne8maSegQX4EYWvuEnLfZN1vqsMpWjvX41hjibW5HLANakxLiZDLUmPumumtuvmQtMckKjMfiy1g1UoekjU1Nmmks7Z4EY8QRXY6xmsEiSxX8ewutF2PHcLPxKTykePp5vZUh8DBbedEXFusZgJtSnNQGpjSgf2CRXjnzxhWgJQEbFrsoLEq2yFTE4nAsAr4x61AS7k6Aw8VQhDAgqPLLXJ8iVWL6SQJuLdcfyU");
        String decodeStr = new String(decode);
        String[] split = decodeStr.split("&");
        System.out.println();
        System.out.println("========================================");
        for (String s : split) {
            System.out.println(s);
        }
        System.out.println("========================================");
        System.out.println();
    }
}