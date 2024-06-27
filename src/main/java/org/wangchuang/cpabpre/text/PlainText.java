package org.wangchuang.cpabpre.text;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import org.wangchuang.cpabpre.parameter.PublicKey;

import java.nio.charset.StandardCharsets;

@Data
public class PlainText {
    private Element messageValue;
    private String messageStr;

    public PlainText(String messageStr, PublicKey publicKey) {
        this(messageStr, publicKey.getPairingParameter().getGT());
    }
    public PlainText(PublicKey publicKey) {
        this(publicKey.getPairingParameter().getGT());
    }

    private PlainText(String messageStr, Field GT) {
        this.messageStr = messageStr;
        this.messageValue = GT.newElementFromBytes(messageStr.getBytes(StandardCharsets.UTF_8)).getImmutable();
    }
    private PlainText(Field GT){
        this.messageStr="";
        this.messageValue=GT.newRandomElement().getImmutable();
    }

}
