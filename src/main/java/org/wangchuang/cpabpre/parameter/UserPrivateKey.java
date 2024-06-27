package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.wangchuang.cpabpre.attribute.Attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UserPrivateKey {

    private Element K;
    private Element L;

    private Element K_x;


    private PairingParameter pairingParameter;
//    public Map<Attribute,Element> K_x_map;

    private List<Attribute> userAttributes;




    private UserPrivateKey() {

//        K_x_map = new HashMap<>();
    }


//    public void putKx(Attribute attribute,Element K_x){
//        K_x_map.put(attribute,K_x);
//    }
//
//
//
//    public Element getKx(Attribute attribute){
//        for (Attribute attribute1 : K_x_map.keySet()) {
//            if (attribute1.equals(attribute)){
//                return K_x_map.get(attribute1);
//            }
//        }
//        return null;
//    }



    public static UserPrivateKey build(MasterPrivateKey masterPrivateKey, List<Attribute> attributes,List<AAK> AAS){
        UserPrivateKey userPrivateKey = new UserPrivateKey();
        userPrivateKey.setPairingParameter(masterPrivateKey.getPairingParameter());
        userPrivateKey.setUserAttributes(attributes);
//        Element ts = masterPrivateKey.getPairingParameter().getZp().newRandomElement().getImmutable();
//        Element g_alpha_msk = masterPrivateKey.getG_alpha();
//        Element g_alpha = masterPrivateKey.getPairingParameter().getG_alpha();
//        Element g = masterPrivateKey.getPairingParameter().getGenerator();
//        Element K=g_alpha.powZn(ts).mul(g_alpha_msk).getImmutable();
//        userPrivateKey.setK(K);
//        Element L=g.powZn(ts).getImmutable();
//        userPrivateKey.setL(L);
        Element K=masterPrivateKey.getPairingParameter().getG().newOneElement().getImmutable();
        Element L=masterPrivateKey.getPairingParameter().getG().newOneElement().getImmutable();
        Element K_x=masterPrivateKey.getPairingParameter().getG().newOneElement().getImmutable();

        for (Attribute attribute : attributes) {
            for (AAK aa : AAS) {
                for (Attribute aaAttribute : aa.getAttributes()) {
                    if (aaAttribute.equals(attribute)){
                        K=K.mul(aa.getApks().get(aaAttribute).getK()).getImmutable();
                        L=L.mul(aa.getApks().get(aaAttribute).getL()).getImmutable();
                        K_x=K_x.mul(aa.getApks().get(aaAttribute).getK_x()).getImmutable();
//                        userPrivateKey.putKx(attribute,aa.getApks().get(aaAttribute).getK_x());
                    }
                }
            }
        }
        userPrivateKey.setK(K.mul(masterPrivateKey.getPairingParameter().getGenerator().powZn(masterPrivateKey.getAlpha())));
        userPrivateKey.setL(L);
        userPrivateKey.setK_x(K_x);
//        for (Attribute attribute : attributes){
//            Element K_x= masterPrivateKey.hash1(attribute.getAttributeName()).powZn(ts).getImmutable();
//            userPrivateKey.putKx(attribute,K_x);
//        }
        return userPrivateKey;
    }


}
