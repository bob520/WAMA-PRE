package org.wangchuang.cpabpre.lsss;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;


import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.parameter.PublicKey;

import java.util.*;

@Data
public class OldAccessMatrix {
    private Map<Attribute,Element[]> V_x_map;
    private Map<Attribute, Element> L_x_map;
    private OldAccessMatrix(){
        L_x_map= new HashMap<>();
        V_x_map=new HashMap<>();
    }

    public void putLx(Attribute attribute,Element L_x){
        L_x_map.put(attribute,L_x);
    }



    public Element getLx(Attribute attribute){
        for (Attribute attribute1 : L_x_map.keySet()) {
            if (attribute1.equals(attribute)){
                return L_x_map.get(attribute1);
            }
        }
        return null;
    }
    public void putVx(Attribute attribute,Element[] V_x){
        V_x_map.put(attribute,V_x);
    }



    public Element[] getVx(Attribute attribute){
        for (Attribute attribute1 :V_x_map.keySet()) {
            if (attribute1.equals(attribute)){
                return V_x_map.get(attribute1);
            }
        }
        return null;
    }
    public static OldAccessMatrix build(PublicKey publicKey, OldAccessControlTreeNode root, Element s){
        int counter=1;
        OldAccessMatrix accessMatrix=new OldAccessMatrix();
        Queue<OldAccessControlTreeNode> queue = new LinkedList<>();
        queue.offer(root);
        root.setVector(new Element[]{publicKey.getPairingParameter().getZp().newElement(1).getImmutable()});

        while (!queue.isEmpty()) {
            OldAccessControlTreeNode node = queue.poll();
            Element[] vector = node.getVector();
            if (node.getGateType().equals("OR")) {
                OldAccessControlTreeNode left = node.getChildren().get(0);
                OldAccessControlTreeNode right = node.getChildren().get(1);
                left.setVector(vector);
                right.setVector(vector);
            } else if (node.getGateType().equals("AND")) {
                Element[] paddedVector = padVector(publicKey,vector,counter);
                Element[] paddedZero = padVector(publicKey,new Element[]{},counter);
                OldAccessControlTreeNode left = node.getChildren().get(0);
                OldAccessControlTreeNode right = node.getChildren().get(1);
                left.setVector(concatVectors(paddedZero, new Element[]{publicKey.getPairingParameter().getZp().newElement(-1).getImmutable()}));
                right.setVector(concatVectors(paddedVector, new Element[]{publicKey.getPairingParameter().getZp().newElement(1).getImmutable()}));
//                node.setVector(concatVectors(paddedVector, new int[]{-1}));
                counter++;
            }else{
                //叶子节点
                accessMatrix.putVx(node.getAttribute(),padVector(publicKey,vector,counter));
            }


            for (OldAccessControlTreeNode child : node.getChildren()) {
                queue.offer(child);
            }
        }
        Element[] s_v=new Element[counter];
        s_v[0]=s;
        for (int i = 1; i < counter; i++) {
            s_v[i]=publicKey.getPairingParameter().getZp().newRandomElement().getImmutable();
        }

        for (Attribute attribute : accessMatrix.V_x_map.keySet()) {
           Element[] v=accessMatrix.V_x_map.get(attribute);
           Element res=publicKey.getPairingParameter().getZp().newElement().getImmutable();
            for (int i = 0; i < v.length; i++) {
                res=res.add(v[i].mul(s_v[i]).getImmutable()).getImmutable();
            }
            accessMatrix.L_x_map.put(attribute,res);
        }
        return accessMatrix;



    }



    public static Element[] padVector(PublicKey publicKey,Element[] vector,int counter) {
        Element[] paddedVector = Arrays.copyOf(vector, counter);
        for (int i = vector.length; i < counter; i++) {
            paddedVector[i] = publicKey.getPairingParameter().getZp().newZeroElement().getImmutable();
        }
        return paddedVector;
    }


    public static Element[] concatVectors(Element[] v1, Element[] v2) {
        Element[] result = new Element[v1.length + v2.length];
        System.arraycopy(v1, 0, result, 0, v1.length);
        System.arraycopy(v2, 0, result, v1.length, v2.length);
        return result;
    }
    public static Element[][] vectorToMatrix(Element[] matrix){
        Element[][] res=new Element[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            Element[] a={matrix[i]};
            res[i]=a;
        }
        return res;
    }
    public static void printMatrix(Element[][] matrix){
        for(int i = 0;i<matrix.length;i++) {
            for(int j =0;j<matrix[0].length;j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}
