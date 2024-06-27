package org.wangchuang.cpabpre.utils;

import java.nio.charset.Charset;

public class ConvertUtils {
    /**
     * Zero to string conversion before and after removing the bit array
     * @param bytes
     * @return
     */
    public static String byteToStr(byte[] bytes){
        int startIndex = 0;
        int endIndex = bytes.length;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0){
                startIndex = i;
                break;
            }
        }

        for (int i = bytes.length - 1; i >= 0; i--) {
            if (bytes[i] != 0){
                endIndex = i;
                break;
            }
        }
        return new String(bytes,startIndex,endIndex - startIndex + 1, Charset.defaultCharset());
    }
}