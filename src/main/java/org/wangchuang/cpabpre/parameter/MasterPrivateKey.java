package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MasterPrivateKey extends Key{


    /**
     * alpha
     */
    private Element alpha;
    /**
     * beta
     */
    private Element beta;


    private MasterPrivateKey(){

    }

    private MasterPrivateKey(PairingParameter parameter){
        super(parameter);
    }

    public static MasterPrivateKey build(PairingParameter parameter){
        MasterPrivateKey masterPrivateKey = new MasterPrivateKey(parameter);
        masterPrivateKey.setAlpha(parameter.getZp().newRandomElement().getImmutable());
        masterPrivateKey.setBeta(parameter.getZp().newRandomElement().getImmutable());
        return masterPrivateKey;
    }


}