package org.wangchuang.cpabpre.parameter;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.PairingParameters;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wangchuang.cpabpre.utils.Util;

@Data
@NoArgsConstructor
public abstract class Key {
    private PairingParameter pairingParameter;

    protected Key(PairingParameter pairingParameter) {
        this.pairingParameter = pairingParameter;
    }


    public Element hash1(String attribute) {
        return Util.hashFromStringToG1(attribute);
    }

    public Element hash2(Element element) {
        return Util.hashFromGTToZp(element);
    }
}
