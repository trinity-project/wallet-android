package org.trinity.util.convert;

public final class ArrayUtil {
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static byte[] arrayLinker(byte[] front, byte[] tail) {
        int lenFront = front.length;
        int lenTail = tail.length;
        byte[] newArray = new byte[lenFront + lenTail];
        System.arraycopy(front, 0, newArray, 0, lenFront);
        System.arraycopy(tail, 0, newArray, lenFront, lenTail);
        return newArray;
    }
}
