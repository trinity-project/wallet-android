package org.trinity.util;

import java.util.UUID;

public class UUIDUtil {
    public static String getRandomLowerNoLine() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
