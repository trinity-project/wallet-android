package org.trinity.util.algorithm;

import java.util.UUID;

public final class UUIDUtil {
    public static String getRandomLowerNoLine() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
