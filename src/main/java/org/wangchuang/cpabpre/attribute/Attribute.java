package org.wangchuang.cpabpre.attribute;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import org.wangchuang.cpabpre.parameter.PublicKey;
import org.wangchuang.cpabpre.utils.Util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


@Data
public class Attribute {

    private Element attributeValue;
    private String attributeName;

    private int weight;

    public Attribute(String attributeName, PublicKey publicKey,int weight){
        this(attributeName,publicKey.getPairingParameter().getG(),weight);
    }

    public Attribute(String attributeName, Field G,int weight){
        this.attributeName = attributeName;
        this.attributeValue = Util.hashFromStringToG1(attributeName);
        this.weight=weight;
    }

    @Override
    public String toString(){
        return attributeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute attribute1 = (Attribute) o;
        return Objects.equals(attributeValue, attribute1.attributeValue) && Objects.equals(attributeName, attribute1.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeValue, attributeName);
    }


    public static byte[] sha256(String attributeName){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(attributeName.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return messageDigest.digest();
    }
}
