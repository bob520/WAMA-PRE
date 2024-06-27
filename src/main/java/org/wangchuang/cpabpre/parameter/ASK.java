package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

import java.util.Map;


@Data
public class ASK {
    public Element h;
    private ASK(){
    }

    public static ASK build(PublicKey publicKey){
        ASK ask = new ASK();
        Element h = publicKey.getPairingParameter().getZp().newRandomElement().getImmutable();
        ask.setH(h);
        return ask;
    }
}