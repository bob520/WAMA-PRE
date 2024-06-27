package org.wangchuang.cpabpre.text;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.ToString;
import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.lsss.AccessMatrix;
import org.wangchuang.cpabpre.lsss.OldAccessMatrix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
@ToString
public class CipherText {
    //gt
    private Element A1;
    //g0
    private Element A2;
    //g
    private Element A3;
    //g0
    private Map<Attribute,Element> B_x_map;
    private Map<Attribute,Element> C_x_map;
    //访问树
    private AccessMatrix accessMatrix;

    public void putBx(Attribute attribute,Element bx){
        B_x_map.put(attribute,bx);
    }

    public void putCx(Attribute attribute,Element cx){
        C_x_map.put(attribute,cx);
    }

    public Element getBx(Attribute attribute){
//        return B_x_map.get(attribute);
        for (Attribute attribute1 : B_x_map.keySet()) {
            if(attribute1.getAttributeName()==attribute.getAttributeName()){
                if(attribute1.getWeight()<=attribute.getWeight()){
                    return B_x_map.get(attribute1);
                }
            }

        }
        return null;

    }

    public Element getCx(Attribute attribute){
//        return C_x_map.get(attribute);
        for (Attribute attribute1 : C_x_map.keySet()) {
            if(attribute1.getAttributeName()==attribute.getAttributeName()){
                if(attribute1.getWeight()<=attribute.getWeight()){
                    return C_x_map.get(attribute1);
                }
            }

        }
        return null;
    }

    public CipherText() {
        B_x_map = new HashMap<>();
        C_x_map = new HashMap<>();
    }


}
