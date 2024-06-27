package org.wangchuang.cpabpre.parameter;

import lombok.Data;
import org.wangchuang.cpabpre.attribute.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class AAK {
    public List<Attribute> attributes;
    public Map<Attribute,ASK> asks;
    public Map<Attribute,APK> apks;
    public AAK(){

        this.apks=new HashMap<>();
        this.asks=new HashMap<>();
    }
    public static AAK build(PublicKey publicKey,List<Attribute> attributes){
        AAK aak = new AAK();
        aak.setAttributes(attributes);
        for (Attribute attribute : attributes) {
            ASK ask = ASK.build(publicKey);
            APK apk = APK.build(publicKey, ask, attribute);
            aak.getApks().put(attribute,apk);
            aak.getAsks().put(attribute,ask);
        }
        return  aak;
    }
}
