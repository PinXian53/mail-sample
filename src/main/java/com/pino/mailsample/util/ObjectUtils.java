package com.pino.mailsample.util;

public class ObjectUtils {

    public static void checkNull(Object obj, String errMsg) {
        if (obj == null) {
            throw new IllegalArgumentException(errMsg);
        }
    }
}
