package org.wangchuang.cpabpre.text;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.lsss.AccessMatrix;

import java.util.HashMap;
import java.util.Map;

@Data
public class ReCipherText {

    private Element A1;

    private Element A3;
    private Element A4;

    private CipherText rk4;
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
        return B_x_map.get(attribute);
    }

    public Element getCx(Attribute attribute){
        return C_x_map.get(attribute);
    }

    public ReCipherText() {
        B_x_map = new HashMap<>();
        C_x_map = new HashMap<>();
    }
}
