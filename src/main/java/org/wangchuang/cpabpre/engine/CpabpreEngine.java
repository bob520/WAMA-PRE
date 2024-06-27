package org.wangchuang.cpabpre.engine;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.lsss.AccessControlTreeNode;
import org.wangchuang.cpabpre.lsss.AccessMatrix;
import org.wangchuang.cpabpre.lsss.OldAccessControlTreeNode;
import org.wangchuang.cpabpre.lsss.OldAccessMatrix;
import org.wangchuang.cpabpre.parameter.*;
import org.wangchuang.cpabpre.text.CipherText;
import org.wangchuang.cpabpre.text.PlainText;
import org.wangchuang.cpabpre.text.ReCipherText;
import org.wangchuang.cpabpre.utils.ConvertUtils;

import java.util.*;

public class CpabpreEngine {
    public UserPrivateKey keyGen(MasterPrivateKey masterPrivateKey, List<Attribute> attributes, List<AAK> AAS) {
        return UserPrivateKey.build(masterPrivateKey, attributes,AAS);
    }
    public CipherText encrypt(PublicKey pk, PlainText plainText, AccessControlTreeNode root) {

        Element s = getRandomElementInZr(pk);
       AccessMatrix accessMatrix= AccessMatrix.build(pk,root,s);

        CipherText cipherText = new CipherText();
//        System.out.println(plainText.getMessageValue());

        Element A1=(plainText.getMessageValue().mul(pk.getEgg_a().powZn(s).getImmutable())).getImmutable();
//        Pairing pairing=pk.getPairingParameter().getPairing();
//        Element g_alpha=pk.getPairingParameter().getG_alpha();
//        Element ts=sk.getTs();
//        Element g=pk.getPairingParameter().getGenerator();
//        Element g_a_ts=g_alpha.powZn(ts).getImmutable();
//        Element e_g_gts=pairing.pairing(g,g_a_ts).getImmutable();
//        System.out.println(e_g_gts);



        cipherText.setA1(A1);

        Element A2 = pk.getPairingParameter().getGenerator().powZn(s).getImmutable();
        cipherText.setA2(A2);

        Element A3=pk.getPairingParameter().getGenerator1().powZn(s).getImmutable();
        cipherText.setA3(A3);

//        Element g_ts=g.powZn(ts).getImmutable();

        int counter=accessMatrix.getCounter();
        Element[] s_v=new Element[counter];
        s_v[0]=s;
        for (int i = 1; i < counter; i++) {
            s_v[i]=pk.getPairingParameter().getZp().newRandomElement().getImmutable();
        }


        for (Attribute attribute :accessMatrix.getV_x_map().keySet()) {
            int[] v=accessMatrix.getVx(attribute);
            Element res=pk.getPairingParameter().getZp().newElement().getImmutable();
            for (int i = 0; i < v.length; i++) {
                res=res.add(s_v[i].mul(v[i]).getImmutable()).getImmutable();
            }
            Element l= res;
            Element r=getRandomElementInZr(pk);
            Element g_a=pk.getPairingParameter().getG_a();
            Element B=g_a.powZn(l).getImmutable().mul(pk.getG_beta().powZn(r).getImmutable().invert().getImmutable()).getImmutable();
            Element C=pk.getPairingParameter().getGenerator().powZn(r).getImmutable();
            cipherText.putBx(attribute,B);
            cipherText.putCx(attribute,C);
        }

//        for (Attribute attribute : accessMatrix.getL_x_map().keySet()) {
//            Element l= accessMa
//
//
//            trix.getLx(attribute);
//            Element r=getRandomElementInZr(pk);
//            Element g_a=pk.getPairingParameter().getG_alpha();
//            Element B=g_a.powZn(l).getImmutable().mul(pk.hash1(attribute.getAttributeValue()).powZn(r).getImmutable().invert().getImmutable()).getImmutable();
//            Element C=pk.getPairingParameter().getGenerator().powZn(r).getImmutable();
////            Element a=pairing.pairing(sk.getKx(attribute).powZn(r).getImmutable().invert().getImmutable(),g_ts);
////            Element b=pairing.pairing(g.powZn(r).getImmutable(),sk.getKx(attribute).powZn(r).getImmutable());
////            System.out.println("test"+a.mul(b).getImmutable());
//            cipherText.putBx(attribute,B);
//            cipherText.putCx(attribute,C);
//        }

//        Element a=pairing.pairing(g,g).powZn(ts).getImmutable();
//        Element b=pairing.pairing(g,g).powZn(ts).invert().getImmutable();

//        System.out.println("test:"+a.mul(b).getImmutable());

        cipherText.setAccessMatrix(accessMatrix);
        return cipherText;
    }
    private Element getRandomElementInZr(PublicKey publicKey) {
        return publicKey.getPairingParameter().getZp().newRandomElement().getImmutable();
    }



    public ReKey reKeyGen(PublicKey pk,UserPrivateKey sk,AccessControlTreeNode root){

        return ReKey.build(pk,sk,root,this);

    }


    public ReCipherText reEncrypt(PublicKey pk,ReKey rk,CipherText ct){
        Pairing pairing=pk.getPairingParameter().getPairing();
        Element A1=ct.getA1();
        Element A2=ct.getA2();
        Element A3=ct.getA3();
        Element rk1=rk.getRk1();
        Element rk2=rk.getRk2();
        Element rk3=rk.getRk3();
        List<Attribute> attributes = rk.getAttributes();
        Map<Attribute,Element> B_x_map=ct.getB_x_map();
        Map<Attribute,Element> C_x_map=ct.getC_x_map();
        CipherText rk4=rk.getRk4();
        Element e_A2_rk1=pairing.pairing(A2,rk1).getImmutable();
        Element e_A3_rk2=pairing.pairing(A3,rk2);


//        HashMap<Attribute,int[]> User_V_x_map=new HashMap<>();
        AccessMatrix o_accessMatrix= ct.getAccessMatrix();

        List<Attribute> accessAttributes=new ArrayList<>();
        for (Attribute attribute : attributes) {
            if(o_accessMatrix.getVx(attribute)!=null) accessAttributes.add(attribute);
        }
        if(accessAttributes.isEmpty()){
            return null;
        }
        int[][] u_v_m=new int[accessAttributes.size()][];
        int co=0;
        for (Attribute attribute : accessAttributes) {
            u_v_m[co]=o_accessMatrix.getVx(attribute);
            co++;
        }
        double[][] u_v_m_T=transpose(u_v_m);
        double[] o_z_v=padVector(new double[]{1},u_v_m_T.length);
        double [][] o_z_m=new double[o_z_v.length][1];
        for (int i = 0; i <o_z_v.length ; i++) {
            o_z_m[i][0]=o_z_v[i];
        }
        RealMatrix A = MatrixUtils.createRealMatrix(u_v_m_T);
        RealMatrix C_M = MatrixUtils.createRealMatrix(o_z_m);


        QRDecomposition qr = new QRDecomposition(A);
        RealMatrix Q = qr.getQ();
        RealMatrix R = qr.getR();

        RealMatrix RInv = new QRDecomposition(R).getSolver().getInverse();

        RealMatrix B_M = RInv.multiply(Q.transpose()).multiply(C_M);


        double[][] w_M=B_M.getData();
        double[] x=new double[w_M.length];
        for (int i = 0; i < x.length; i++) {

            x[i]=Math.round(w_M[i][0]);
        }
        Element[] w=new Element[x.length];
        for (int i = 0; i < x.length; i++) {
            w[i]=pk.getPairingParameter().getZp().newElement((int) x[i]).getImmutable();
        }





        int count=0;
        Element res=pk.getPairingParameter().getGT().newElement().getImmutable();

        for (Attribute attribute : accessAttributes) {
            Element B=ct.getBx(attribute);
            Element C=ct.getCx(attribute);
//            Element Rx=rk.getRx(attribute);
            Element Rx=rk.getR_x();
            Element e_B_rk3=pairing.pairing(B,rk3);
            Element e_C_Rx=pairing.pairing(C,Rx);
            res=res.mul(e_B_rk3.mul(e_C_Rx).getImmutable().powZn(w[count])).getImmutable();
            count++;
        }

        AccessMatrix accessMatrix=rk4.getAccessMatrix();


        Element A4=(e_A2_rk1.div(e_A3_rk2)).div(res).getImmutable();
        ReCipherText reCipherText=new ReCipherText();
        reCipherText.setAccessMatrix(accessMatrix);
        reCipherText.setA1(A1);
        reCipherText.setA3(A3);
        reCipherText.setB_x_map(B_x_map);
        reCipherText.setC_x_map(C_x_map);
        reCipherText.setA4(A4);
        reCipherText.setRk4(rk4);

        return  reCipherText;
    }


    public Element reDecrypt(PublicKey pk,UserPrivateKey sk,ReCipherText rct){
        Element xigema=decrypt(pk,sk,rct.getRk4());
        if(xigema==null){
            return null;
        }
        Element A1=rct.getA1();
        Element A4= rct.getA4();
        Element h2_xigema_invert=pk.hash2(xigema).invert().getImmutable();
        Element res=A1.div(A4.powZn(h2_xigema_invert).getImmutable()).getImmutable();
        return  res;
    }


    public String decryptToStr(PublicKey publicKey, UserPrivateKey userPrivateKey, ReCipherText recipherText){
        Element decrypt = reDecrypt(publicKey, userPrivateKey,recipherText);
        if (decrypt != null){
            return new String(ConvertUtils.byteToStr(decrypt.toBytes()));
        }
        return null;
    }










    public Element decrypt(PublicKey publicKey, UserPrivateKey userPrivateKey, CipherText cipherText) {




//        HashMap<Attribute,Element> User_L_x_map=new HashMap<>();
//        HashMap<Attribute,int[]> User_V_x_map=new HashMap<>();
        AccessMatrix accessMatrix= cipherText.getAccessMatrix();
//        System.out.println(userPrivateKey.getUserAttributes());

        List<Attribute> accessAttributes=new ArrayList<>();
        for (Attribute attribute : userPrivateKey.getUserAttributes()) {
//            User_L_x_map.put(attribute,accessMatrix.getL_x_map().get(attribute));

            if(accessMatrix.getVx(attribute)!=null) accessAttributes.add(attribute);
        }
//        System.out.println(User_V_x_map);

        if (accessAttributes.isEmpty()){
            return null;
        }else{
            System.out.println("access attr："+accessAttributes);
        }
        int[][] u_v_m=new int[accessAttributes.size()][];
        int co=0;
        for (Attribute attribute : accessAttributes) {
            u_v_m[co]=accessMatrix.getVx(attribute);
            co++;
        }


        double[][] u_v_m_T=transpose(u_v_m);
        double[] o_z_v=padVector(new double[]{1},u_v_m_T.length);
        double [][] o_z_m=new double[o_z_v.length][1];
        for (int i = 0; i <o_z_v.length ; i++) {
            o_z_m[i][0]=o_z_v[i];
        }
        RealMatrix A = MatrixUtils.createRealMatrix(u_v_m_T);
        RealMatrix C_M = MatrixUtils.createRealMatrix(o_z_m);

        // Perform QR decomposition on matrix A
        QRDecomposition qr = new QRDecomposition(A);
        RealMatrix Q = qr.getQ();
        RealMatrix R = qr.getR();

        RealMatrix RInv = new QRDecomposition(R).getSolver().getInverse();

        RealMatrix B_M = RInv.multiply(Q.transpose()).multiply(C_M);


        double[][] w_M=B_M.getData();
        double[] x=new double[w_M.length];
        for (int i = 0; i < x.length; i++) {

            x[i]=Math.round(w_M[i][0]);
        }
        if(Arrays.stream(x).sum()==0){
            return null;
        }

        Element[] w=new Element[x.length];
        for (int i = 0; i < x.length; i++) {
            w[i]=publicKey.getPairingParameter().getZp().newElement((int) x[i]).getImmutable();
        }



        Element A1=cipherText.getA1();
        Element A2=cipherText.getA2();
        Element K=userPrivateKey.getK();
        Pairing pairing=publicKey.getPairingParameter().getPairing();
        Element L=userPrivateKey.getL();
        Element e_A2_K=pairing.pairing(A2,K);
        Element res=publicKey.getPairingParameter().getGT().newOneElement();

        int count=0;

        for (Attribute attribute : accessAttributes) {
            Element B=cipherText.getBx(attribute);
            Element C=cipherText.getCx(attribute);
            Element Kx=userPrivateKey.getK_x();
            Element e_B_L=pairing.pairing(B,L).getImmutable();
            Element e_C_Kx=pairing.pairing(C,Kx).getImmutable();
            //            res=res.mul((e_B_L.mul(e_C_Kx).getImmutable()).powZn(w[count])).getImmutable();
            res=res.mul(e_B_L.mul(e_C_Kx).getImmutable().powZn(w[count])).getImmutable();


            count++;

        }
        Element res3=A1.div(e_A2_K.div(res)).getImmutable();
        return res3;

    }

    public static void printMatrix(double[][] matrix){
        for(int i = 0;i<matrix.length;i++) {
            for(int j =0;j<matrix[0].length;j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.println();
        }
        System.out.println();
    }
    public static void printMatrix(int[][] matrix){
        for(int i = 0;i<matrix.length;i++) {
            for(int j =0;j<matrix[0].length;j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.println();
        }
        System.out.println();
    }
    public String decryptToStr(PublicKey publicKey, UserPrivateKey userPrivateKey, CipherText cipherText){
        Element decrypt = decrypt(publicKey, userPrivateKey, cipherText);
        if (decrypt != null){
            return new String(ConvertUtils.byteToStr(decrypt.toBytes()));
        }
        return null;
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

    public static double[][] transpose(int[][] matrix) {
        double res[][] = new double[matrix[0].length][matrix.length];

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j <  matrix[0].length; j++){
                res[j][i] = matrix[i][j];
            }
        }

        return res;

    }
    public static double[] padVector(double[] vector,int counter) {
        double[] paddedVector = Arrays.copyOf(vector, counter);
        for (int i = vector.length; i < counter; i++) {
            paddedVector[i] = 0;
        }
        return paddedVector;
    }

}
