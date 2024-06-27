package org.wangchuang.cpabpre.lsss;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.parameter.PublicKey;

import java.util.*;

@Data
public class AccessMatrix {
    private Map<Attribute,int[]> V_x_map;
    private int counter;
//    private Map<Attribute, Element> L_x_map;
    private AccessMatrix(){
//        L_x_map= new HashMap<>();
        V_x_map=new HashMap<>();
    }

//    public void putLx(Attribute attribute,Element L_x){
//        L_x_map.put(attribute,L_x);
//    }



//    public Element getLx(Attribute attribute){
//        for (Attribute attribute1 : L_x_map.keySet()) {
//            if (attribute1.equals(attribute)){
//                return L_x_map.get(attribute1);
//            }
//        }
//        return null;
//    }
    public void putVx(Attribute attribute,int[] V_x){
        V_x_map.put(attribute,V_x);
    }



    public int[] getVx(Attribute attribute){
        for (Attribute attribute1 :V_x_map.keySet()) {
//            if (attribute1.equals(attribute)){
//                return V_x_map.get(attribute1);
//            }
            if (attribute1.getAttributeName()==attribute.getAttributeName()){
                if (attribute1.getWeight()<=attribute.getWeight()){
                    return V_x_map.get(attribute1);
                }
            }
        }
        return null;
    }
    public static AccessMatrix build(PublicKey publicKey, AccessControlTreeNode root, Element s){
        int counter=1;
        AccessMatrix accessMatrix=new AccessMatrix();
        Queue<AccessControlTreeNode> queue = new LinkedList<>();
        queue.offer(root);
        root.setVector(new int[]{1});

        while (!queue.isEmpty()) {
            AccessControlTreeNode node = queue.poll();
            int[] vector = node.getVector();
            if (node.getGateType().equals("OR")) {
                AccessControlTreeNode left = node.getChildren().get(0);
                AccessControlTreeNode right = node.getChildren().get(1);
                left.setVector(vector);
                right.setVector(vector);
            } else if (node.getGateType().equals("AND")) {
                int[] paddedVector = padVector(publicKey,vector,counter);

                AccessControlTreeNode left = node.getChildren().get(0);
                AccessControlTreeNode right = node.getChildren().get(1);
                left.setVector(concatVectors(new int[counter], new int[]{-1}));
                right.setVector(concatVectors(paddedVector, new int[]{1}));
//                node.setVector(concatVectors(paddedVector, new int[]{-1}));
                counter++;
            }else{
                //叶子节点
                accessMatrix.putVx(node.getAttribute(),vector);
            }

            // 将子节点加入队列
            for (AccessControlTreeNode child : node.getChildren()) {
                queue.offer(child);
            }
        }

        for (Attribute attribute : accessMatrix.getV_x_map().keySet()) {
            accessMatrix.putVx(attribute,padVector(publicKey,accessMatrix.getVx(attribute),counter));
        }
        accessMatrix.setCounter(counter);
//        Element[] s_v=new Element[counter];
//        s_v[0]=s;
//        for (int i = 1; i < counter; i++) {
//            s_v[i]=publicKey.getPairingParameter().getZp().newRandomElement().getImmutable();
//        }
//
//        for (Attribute attribute : accessMatrix.V_x_map.keySet()) {
//           int[] v=accessMatrix.V_x_map.get(attribute);
//           Element res=publicKey.getPairingParameter().getZp().newElement().getImmutable();
//            for (int i = 0; i < v.length; i++) {
//                res=res.add(s_v[i].mul(v[i]).getImmutable()).getImmutable();
//            }
//            accessMatrix.L_x_map.put(attribute,res);
//        }
        return accessMatrix;





    }



    public static int[] padVector(PublicKey publicKey,int[] vector,int counter) {
        int[] paddedVector = Arrays.copyOf(vector, counter);
        for (int i = vector.length; i < counter; i++) {
            paddedVector[i] = 0;
        }
        return paddedVector;
    }


    public static int[] concatVectors(int[] v1, int[] v2) {
        int[] result = new int[v1.length + v2.length];
        System.arraycopy(v1, 0, result, 0, v1.length);
        System.arraycopy(v2, 0, result, v1.length, v2.length);
        return result;
    }
    public static int[][] vectorToMatrix(int[] matrix){
        int[][] res=new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            int[] a={matrix[i]};
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
