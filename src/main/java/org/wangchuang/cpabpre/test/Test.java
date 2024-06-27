package org.wangchuang.cpabpre.test;

import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.engine.CpabpreEngine;
import org.wangchuang.cpabpre.lsss.AccessControlTreeNode;
import org.wangchuang.cpabpre.parameter.AAK;
import org.wangchuang.cpabpre.parameter.ReKey;
import org.wangchuang.cpabpre.parameter.SystemKey;
import org.wangchuang.cpabpre.parameter.UserPrivateKey;
import org.wangchuang.cpabpre.text.CipherText;
import org.wangchuang.cpabpre.text.PlainText;
import org.wangchuang.cpabpre.text.ReCipherText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args)  {
        test1();

    }

    private static void test2(){

    }

    private static void test1() {


        SystemKey systemKey = SystemKey.build();

//        Element a=systemKey.getPublicKey().getPairingParameter().getZp().newRandomElement();
//        Element b=systemKey.getPublicKey().getPairingParameter().getG().newRandomElement();
//        Element c=systemKey.getPublicKey().getPairingParameter().getGT().newRandomElement();
//        Element d=systemKey.getPublicKey().getPairingParameter().getG().newRandomElement();
//        Element b_a=b.powZn(a).getImmutable();
//        Element d_a=d.powZn(a).getImmutable();
//        Element egg=systemKey.getPublicKey().getPairingParameter().getPairing().pairing(b_a,d_a).getImmutable();
//        Element egg2=systemKey.getPublicKey().getPairingParameter().getPairing().pairing(d_a,b_a).getImmutable();
//
//        System.out.println(egg.isEqual(egg2));
//        System.out.println(egg.mul(egg).getImmutable());
//        System.out.println(systemKey.getPublicKey().getPairingParameter().getPairing().pairing(b,b));
//        System.out.println(b);
//        System.out.println(d);

        Attribute A=new Attribute("a",systemKey.getPublicKey(),1);
        Attribute B=new Attribute("b",systemKey.getPublicKey(),1);
        Attribute C=new Attribute("c",systemKey.getPublicKey(),1);
        Attribute D=new Attribute("d",systemKey.getPublicKey(),1);
        Attribute E=new Attribute("e",systemKey.getPublicKey(),1);
        Attribute F=new Attribute("f",systemKey.getPublicKey(),1);
        Attribute G=new Attribute("g",systemKey.getPublicKey(),1);
        Attribute H=new Attribute("h",systemKey.getPublicKey(),1);
        Attribute O1=new Attribute("i",systemKey.getPublicKey(),1);
        Attribute O2=new Attribute("i",systemKey.getPublicKey(),2);
        Attribute O3=new Attribute("i",systemKey.getPublicKey(),3);
        Attribute O4=new Attribute("i",systemKey.getPublicKey(),4);

        List<Attribute> AA1Attrs= Arrays.asList(A,B,C,D);
        List<Attribute> AA2Attrs= Arrays.asList(E,F,G,H);
        List<Attribute> AA3Attrs=Arrays.asList(O1,O2,O3,O4);


        AAK aak1 = AAK.build(systemKey.getPublicKey(),AA1Attrs);
        AAK aak2 = AAK.build(systemKey.getPublicKey(),AA2Attrs);
        AAK aak3 = AAK.build(systemKey.getPublicKey(),AA3Attrs);
        List<AAK> AAS=Arrays.asList(aak1,aak2,aak3);


        AccessControlTreeNode root = new AccessControlTreeNode("AND");
        AccessControlTreeNode andNode = new AccessControlTreeNode("AND");
        AccessControlTreeNode attrNode1 = new AccessControlTreeNode(A);
        AccessControlTreeNode attrNode2 = new AccessControlTreeNode(B);
        AccessControlTreeNode andNode1 = new AccessControlTreeNode("AND");
        AccessControlTreeNode attrNode3 = new AccessControlTreeNode(C);
        AccessControlTreeNode attrNode4 = new AccessControlTreeNode(O2);

//        AccessControlTreeNode root = new AccessControlTreeNode("AND");
//        AccessControlTreeNode andNode = new AccessControlTreeNode("AND");
//        AccessControlTreeNode attrNode1 = new AccessControlTreeNode(new Attribute("A",systemKey.getPublicKey()));
//        AccessControlTreeNode attrNode2 = new AccessControlTreeNode(new Attribute("B",systemKey.getPublicKey()));
//        AccessControlTreeNode andNode1 = new AccessControlTreeNode("OR");
//        AccessControlTreeNode attrNode3 = new AccessControlTreeNode(new Attribute("C",systemKey.getPublicKey()));
//        AccessControlTreeNode attrNode4 = new AccessControlTreeNode(new Attribute("D",systemKey.getPublicKey()));
        andNode.addChild(attrNode1);
        andNode.addChild(attrNode2);
        andNode1.addChild(attrNode3);
        andNode1.addChild(attrNode4);
        root.addChild(andNode);
        root.addChild(andNode1);
//        Element s=systemKey.getPublicKey().getPairingParameter().getZp().newRandomElement().getImmutable();
//        System.out.println(s);
//        AccessMatrix accessMatrix=AccessMatrix.build(systemKey.getPublicKey(),root,s);
        CpabpreEngine cpabpreEngine=new CpabpreEngine();
        List<Attribute> userAttributes=new ArrayList<>();
//        userAttributes.add(new Attribute("A",systemKey.getPublicKey()));
//        userAttributes.add(new Attribute("B",systemKey.getPublicKey()));
//        userAttributes.add(new Attribute("C",systemKey.getPublicKey()));
        userAttributes.add(A);

        userAttributes.add(B);
        userAttributes.add(C);
        userAttributes.add(O3);


        List<Attribute> user2Attributes=new ArrayList<>();
        user2Attributes.add(A);
        user2Attributes.add(F);
//        user2Attributes.add(G);
//        user2Attributes.add(H);
        user2Attributes.add(E);


        String plainTextStr = "jjjjjjj";
        PlainText plainText = new PlainText(plainTextStr, systemKey.getPublicKey());

        UserPrivateKey userPrivateKey = cpabpreEngine.keyGen(systemKey.getMasterPrivateKey(), userAttributes,AAS);

        UserPrivateKey sk2 = cpabpreEngine.keyGen(systemKey.getMasterPrivateKey(), user2Attributes,AAS);

        CipherText cipherText = cpabpreEngine.encrypt(systemKey.getPublicKey(), plainText, root);

        String recoverText= cpabpreEngine.decryptToStr(systemKey.getPublicKey(),userPrivateKey,cipherText);
        System.out.println(recoverText);

        String recoverText2= cpabpreEngine.decryptToStr(systemKey.getPublicKey(),sk2,cipherText);
        System.out.println(recoverText2);


        AccessControlTreeNode root2 = new AccessControlTreeNode("OR");
        AccessControlTreeNode andNode2 = new AccessControlTreeNode("AND");
        AccessControlTreeNode attrNode12 = new AccessControlTreeNode(E);
        AccessControlTreeNode attrNode22 = new AccessControlTreeNode(F);
        AccessControlTreeNode andNode12 = new AccessControlTreeNode("AND");
        AccessControlTreeNode attrNode32 = new AccessControlTreeNode(G);
        AccessControlTreeNode attrNode42 = new AccessControlTreeNode(H);


        andNode2.addChild(attrNode12);
        andNode2.addChild(attrNode22);
        andNode12.addChild(attrNode32);
        andNode12.addChild(attrNode42);
        root2.addChild(andNode2);
        root2.addChild(andNode12);



        ReKey rk=cpabpreEngine.reKeyGen(systemKey.getPublicKey(),userPrivateKey,root2);

        ReCipherText reCipherText=cpabpreEngine.reEncrypt(systemKey.getPublicKey(),rk,cipherText);


        String re= cpabpreEngine.decryptToStr(systemKey.getPublicKey(),sk2,reCipherText);
        System.out.println(re);
    }

    public static double[][] gaussianElimination(double[][] Ab) {
        int m = Ab.length;
        int n = Ab[0].length - 1;

        for (int i = 0; i < m; i++) {

            int maxRow = i;
            for (int j = i + 1; j < m; j++) {
                if (Math.abs(Ab[j][i]) > Math.abs(Ab[maxRow][i])) {
                    maxRow = j;
                }
            }


            double[] temp = Ab[i];
            Ab[i] = Ab[maxRow];
            Ab[maxRow] = temp;

            for (int j = i + 1; j < m; j++) {
                double factor = Ab[j][i] / Ab[i][i];
                for (int k = i; k <= n; k++) {
                    Ab[j][k] -= factor * Ab[i][k];
                }
            }
        }

        return Ab;
    }
    public static double[] backSubstitution(double[][] Ab){
        int m = Ab.length;
        int n = Ab[0].length - 1;

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = Ab[i][n] / Ab[i][i];
            for (int j = i - 1; j >= 0; j--) {
                Ab[j][n] -= Ab[j][i] * x[i];
            }
        }

        return x;
    }


    public static double[][] augment(double[][] A, double[] b) {
        int m = A.length;
        int n = A[0].length;
        double[][] Ab = new double[m][n + 1];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                Ab[i][j] = A[i][j];
            }
            Ab[i][n] = b[i];
        }

        return Ab;
    }



    public static class NoSolutionException extends Exception {
        public NoSolutionException(String message) {
            super(message);
        }
    }

}
