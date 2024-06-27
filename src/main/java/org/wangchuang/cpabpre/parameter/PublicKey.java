package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PublicKey extends Key {
    /**
     * 公开的双线性对
     */
    private Element egg_a;
    private Element g_beta;


    private PublicKey() {

    }

    private PublicKey(PairingParameter parameter) {
        super(parameter);
    }


    public static PublicKey build(PairingParameter parameter, MasterPrivateKey msk) {
        PublicKey publicKey = new PublicKey(parameter);
        publicKey.setEgg_a((parameter.getPairing().pairing(parameter.getGenerator(), parameter.getGenerator()).mulZn(msk.getAlpha())).getImmutable());
        publicKey.setG_beta(parameter.getGenerator().powZn(msk.getBeta()));
        return publicKey;
    }

}
