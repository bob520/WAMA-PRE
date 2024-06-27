package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.wangchuang.cpabpre.attribute.Attribute;

import java.util.List;
import java.util.Map;


@Data
public class APK {
    //    public Element[] Ti;
    public Element K;
    public Element L;
    public Element K_x;
    private APK(){

    }
    public static APK build(PublicKey pk, ASK ask, Attribute attributes){
        APK apk = new APK();
        Element K=pk.getPairingParameter().getG_a().powZn(ask.getH()).getImmutable();
        Element L=pk.getPairingParameter().getGenerator().powZn(ask.getH()).getImmutable();
        Element K_x=pk.getG_beta().powZn(ask.getH()).getImmutable();
        apk.setK(K);
        apk.setL(L);
        apk.setK_x(K_x);
        return apk;
    }
}
