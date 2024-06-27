package org.wangchuang.cpabpre.utils;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.parameter.ReKey;
import org.wangchuang.cpabpre.parameter.UserPrivateKey;
import org.wangchuang.cpabpre.text.CipherText;
import org.wangchuang.cpabpre.text.ReCipherText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Util {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static Pairing pairing;


    public static void setPairing(Pairing pairing) {
        Util.pairing = pairing;
    }


    public static String hexBytesToString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public static Element getRandomFromG1() {
        return pairing.getG1().newRandomElement().getImmutable();
    }

    public static Element getOneFromG1() {
        return pairing.getG1().newOneElement().getImmutable();
    }

    public static Element getZeroFromG1() {
        return pairing.getG1().newZeroElement().getImmutable();
    }


    public static Element getRandomFromZp() {
        return pairing.getZr().newRandomElement().getImmutable();
    }

    public static Element getOneFromZp() {
        return pairing.getZr().newOneElement().getImmutable();
    }

    public static Element getZeroFromZp() {
        return pairing.getZr().newZeroElement().getImmutable();
    }

    //H1,H2 : {0, 1}∗ → G1
    public static Element hashFromStringToG1(String str) {
        return pairing.getG1().newElement().setFromHash(str.getBytes(), 0, str.length()).getImmutable();
    }

    public static Element hashFromBytesToG1(byte[] bytes) {
        return pairing.getG1().newElement().setFromHash(bytes, 0, bytes.length).getImmutable();
    }

    //H : {0, 1}∗ → Zp
    public static Element hashFromStringToZp(String str) {
        return pairing.getZr().newElement().setFromHash(str.getBytes(), 0, str.length()).getImmutable();
    }

    public static Element hashFromBytesToZp( byte[] bytes) {
        return pairing.getZr().newElement().setFromHash(bytes, 0, bytes.length).getImmutable();
    }

    //h : G1 → Zp
    public static Element hashFromG1ToZp( Element g1_element) {
        // h(y) : G1 -> Zp
        byte[] g1_bytes = g1_element.getImmutable().toCanonicalRepresentation();
        byte[] zp_bytes = g1_bytes;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            zp_bytes = hasher.digest(g1_bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Element hash_result = pairing.getZr().newElementFromHash(zp_bytes, 0, zp_bytes.length).getImmutable();
        return hash_result;
    }
    //h:GT-> Zp
    public static Element hashFromGTToZp( Element gt_element) {
        // h(y) : G1 -> Zp
        byte[] g1_bytes = gt_element.getImmutable().toCanonicalRepresentation();
        byte[] zp_bytes = g1_bytes;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            zp_bytes = hasher.digest(g1_bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Element hash_result = pairing.getZr().newElementFromHash(zp_bytes, 0, zp_bytes.length).getImmutable();
        return hash_result;
    }


    public static int h1_pai_key(String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            byte[] result = hasher.digest(data.getBytes());
            ByteBuffer wrapped = ByteBuffer.wrap(result);
            return wrapped.getShort();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static List<Integer> pseudoPerm(int key, int n, int c) {
        List<Integer> result = new ArrayList<Integer>(c);
        if(c < n) {
            List<Integer> list = new ArrayList<>(n);
            for(int i = 0; i < n; i ++) {
                list.add(i);
            }
            for(int i = 0; i < key; i ++)
                java.util.Collections.shuffle(list);
            for(int i = 0; i < c; i ++) {
                result.add(list.get(i));
            }
        } else {
            System.out.println(" pseudorandom permutation error!");
        }
        return result;
    }


    public static String h2_f_key(String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            byte[] result = hasher.digest(data.getBytes());
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    public static Element pseudoFunc(String key, int id) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            byte[] hash_bytes = hasher.digest((key + id).getBytes());   //先把G1元素hash成512bits
            return pairing.getZr().newElementFromHash(hash_bytes, 0, hash_bytes.length).getImmutable();
        } catch (Exception e) {
            e.printStackTrace();
            return pairing.getZr().newRandomElement();
        }
    }
    public static String h256_f_string(String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            hasher.update(data.getBytes(StandardCharsets.UTF_8));
            return hexBytesToString(hasher.digest());

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static String writeCipherTextToPropertiesFile(CipherText cipherText, String name) {
        Properties properties = new Properties();


        properties.setProperty("A1", cipherText.getA1().toString());
        properties.setProperty("A2", cipherText.getA2().toString());
        properties.setProperty("A3", cipherText.getA3().toString());


        for (Attribute attribute : cipherText.getB_x_map().keySet()) {
            properties.setProperty("B_x_" + attribute.getAttributeName(), cipherText.getBx(attribute).toString());
        }


        for (Attribute attribute : cipherText.getC_x_map().keySet()) {
            properties.setProperty("C_x_" + attribute.getAttributeName(), cipherText.getCx(attribute).toString());
        }
        String FilePath="D:\\workspace\\cpabpre-less\\data\\cts\\ct_"+name;
        try (FileOutputStream fileOutputStream = new FileOutputStream(FilePath)) {

            properties.store(fileOutputStream, "CipherText");
            return FilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String writeSKToPropertiesFile(UserPrivateKey sk, String name) {
        Properties properties = new Properties();


        properties.setProperty("K", sk.getK().toString());
        properties.setProperty("L", sk.getL().toString());
        properties.setProperty("K_x", sk.getK_x().toString());

        String FilePath="D:\\workspace\\cpabpre-less\\data\\sks\\sk_"+name;
        try (FileOutputStream fileOutputStream = new FileOutputStream(FilePath)) {

            properties.store(fileOutputStream, "UserPrivateKey");
            return FilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String writeReKeyToPropertiesFile(ReKey reKey, String name) {
        Properties properties = new Properties();


        properties.setProperty("rk1", reKey.getRk1().toString());
        properties.setProperty("rk2", reKey.getRk2().toString());
        properties.setProperty("rk3", reKey.getRk3().toString());



        properties.setProperty("rk4_A1", reKey.getRk4().getA1().toString());
        properties.setProperty("rk4_A2", reKey.getRk4().getA2().toString());

        for (Attribute attribute : reKey.getRk4().getB_x_map().keySet()) {
            properties.setProperty("rk4_B_x_" + attribute.getAttributeName(), reKey.getRk4().getBx(attribute).toString());
        }


        for (Attribute attribute : reKey.getRk4().getC_x_map().keySet()) {
            properties.setProperty("rk4_C_x_" + attribute.getAttributeName(), reKey.getRk4().getCx(attribute).toString());
        }


        properties.setProperty("R_x", reKey.getR_x().toString());
        String FilePath="D:\\workspace\\cpabpre-less\\data\\rks\\rk_"+name;
        try (FileOutputStream fileOutputStream = new FileOutputStream(FilePath)) {

            properties.store(fileOutputStream, "ReKey Properties");
            return FilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String  writeReCipherTextToPropertiesFile(ReCipherText reCipherText, String name) {
        Properties properties = new Properties();


        properties.setProperty("A1", reCipherText.getA1().toString());
        properties.setProperty("A3", reCipherText.getA3().toString());
        properties.setProperty("A4", reCipherText.getA4().toString());


        properties.setProperty("rk4_A1", reCipherText.getRk4().getA1().toString());
        properties.setProperty("rk4_A2", reCipherText.getRk4().getA2().toString());


        for (Attribute attribute : reCipherText.getRk4().getB_x_map().keySet()) {
            properties.setProperty("rk4_B_x_" + attribute.getAttributeName(), reCipherText.getRk4().getBx(attribute).toString());
        }


        for (Attribute attribute : reCipherText.getRk4().getC_x_map().keySet()) {
            properties.setProperty("rk4_C_x_" + attribute.getAttributeName(),reCipherText.getRk4().getCx(attribute).toString());
        }


        for (Attribute attribute : reCipherText.getB_x_map().keySet()) {
            properties.setProperty("B_x_" + attribute.getAttributeName(), reCipherText.getB_x_map().get(attribute).toString());
        }


        for (Attribute attribute : reCipherText.getC_x_map().keySet()) {
            properties.setProperty("C_x_" + attribute.getAttributeName(), reCipherText.getC_x_map().get(attribute).toString());
        }

        String FilePath="D:\\workspace\\cpabpre-less\\data\\rcts\\rct_"+name;

        try (FileOutputStream fileOutputStream = new FileOutputStream(FilePath)) {

            properties.store(fileOutputStream, "ReCipherText Properties");
            return FilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static double getFileSizeInBit(String filePath) {
        File file = new File(filePath);


        if (file.exists() && file.isFile()) {

            long fileSizeBytes = file.length();

            return fileSizeBytes;
        } else {

            return -1;
        }
    }


}