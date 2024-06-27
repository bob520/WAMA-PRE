package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import lombok.Data;
import lombok.ToString;
import org.wangchuang.cpabpre.utils.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

@Data
@ToString
public class PairingParameter {
    private Pairing pairing;
    private Field G;
    private Field GT;
    private Field Zp;
    private Element generator;
    private Element generator1;
    private Element g_a;

    private PairingParameter() {

    }


    public static PairingParameter getInstance() {
        PairingParameter pairingParameter = new PairingParameter();
//        int rBits = 160;
//        int qBits = 512;
//        TypeACurveGenerator typeACurveGenerator = new TypeACurveGenerator(rBits, qBits);
//        File file = new File("a.properties");
//        try {
//            FileWriter fileWriter = new FileWriter(file);
//
//            fileWriter.write(typeACurveGenerator.generate().toString());
//            fileWriter.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        Pairing pairing = PairingFactory.getPairing("a.properties");
        pairingParameter.setPairing(pairing);
        pairingParameter.setG(pairing.getG1());
        pairingParameter.setGT(pairing.getGT());
        pairingParameter.setZp(pairing.getZr());
        pairingParameter.setGenerator(pairingParameter.getG().newRandomElement().getImmutable());
        pairingParameter.setGenerator1(pairingParameter.getG().newRandomElement().getImmutable());
        pairingParameter.setG_a(pairingParameter.getGenerator().powZn(pairingParameter.getZp().newRandomElement().getImmutable()));
        Util.setPairing(pairing);
        return pairingParameter;
    }


}
