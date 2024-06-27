package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.engine.CpabpreEngine;
import org.wangchuang.cpabpre.lsss.AccessControlTreeNode;
import org.wangchuang.cpabpre.lsss.AccessMatrix;
import org.wangchuang.cpabpre.text.CipherText;
import org.wangchuang.cpabpre.text.PlainText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ReKey {
    private Element rk1;
    private Element rk2;
    private Element rk3;
    private CipherText rk4;
    private List<Attribute> attributes;
//    private Map<Attribute,Element> R_x_map;
    private  Element R_x;

//    private ReKey(){
//        R_x_map= new HashMap<>();
//    }
//
//    public void putRx(Attribute attribute,Element rx){
//        R_x_map.put(attribute,rx);
//    }
//
//    public Element getRx(Attribute attribute){
//        for (Attribute attribute1 : R_x_map.keySet()) {
//            if (attribute1.equals(attribute)){
//                return R_x_map.get(attribute1);
//            }
//        }
//        return null;
//    }
    public static ReKey build(PublicKey pk, UserPrivateKey sk, AccessControlTreeNode root, CpabpreEngine cpabpreEngine){
        ReKey reKey=new ReKey();
        PlainText xigema = new PlainText(pk);
        Element g=pk.getPairingParameter().getGenerator();
        Element g1=pk.getPairingParameter().getGenerator1();
        Element h2_xigema=pk.hash2(xigema.getMessageValue());
        Element seita=pk.getPairingParameter().getZp().newRandomElement().getImmutable();
        Element rk1=(sk.getK().powZn(h2_xigema)).mul(g1.powZn(seita)).getImmutable();
        Element rk2=g.powZn(seita).getImmutable();
        Element rk3=sk.getL().powZn(h2_xigema).getImmutable();
        CipherText rk4=cpabpreEngine.encrypt(pk,xigema,root);
        rk4.setA3(null);
//        for (Attribute attribute : sk.getK_x_map().keySet()) {
//            Element rx=sk.getKx(attribute).powZn(h2_xigema).getImmutable();
//            reKey.putRx(attribute,rx);
//        }

        Element rx=sk.getK_x().powZn(h2_xigema).getImmutable();
        reKey.setR_x(rx);
        reKey.setRk1(rk1);
        reKey.setRk2(rk2);
        reKey.setRk3(rk3);

        reKey.setRk4(rk4);
        reKey.setAttributes(sk.getUserAttributes());

        return reKey;
    }

}
