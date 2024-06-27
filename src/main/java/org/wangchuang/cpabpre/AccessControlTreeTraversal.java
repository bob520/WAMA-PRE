package org.wangchuang.cpabpre;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.apache.commons.math3.linear.*;

import java.util.*;

public class AccessControlTreeTraversal {

    public static void main(String[] args) {
        // Building an Access Control Tree
        AccessControlTreeNode root = new AccessControlTreeNode("AND");
        AccessControlTreeNode andNode = new AccessControlTreeNode("AND");
        AccessControlTreeNode attrNode1 = new AccessControlTreeNode("A");
        AccessControlTreeNode attrNode2 = new AccessControlTreeNode("B");
        AccessControlTreeNode andNode1 = new AccessControlTreeNode("OR");
        AccessControlTreeNode attrNode3 = new AccessControlTreeNode("C");
        AccessControlTreeNode attrNode4 = new AccessControlTreeNode("D");
        andNode.addChild(attrNode1);
        andNode.addChild(attrNode2);
        andNode1.addChild(attrNode3);
        andNode1.addChild(attrNode4);
        root.addChild(andNode);
        root.addChild(andNode1);

        int counter = 1;
        // Breadth First Traverse Access Control Tree
        HashMap<String,int[]> result = traverse(counter,root);
        int[][] ShareArray = new int[result.size()][];
        int i=result.size();
        for (Map.Entry<String,int[]> entry : result.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " +Arrays.toString(entry.getValue()));
            ShareArray[i-1]=entry.getValue();
            i--;
        }

        Pairing pairing = PairingFactory.getPairing("a.properties");


        //LSSS shared generation matrix

        int row=ShareArray.length;
        int col=ShareArray[0].length;

        Element[] s_v=new Element[col];
        for (int i1 = 0; i1 < s_v.length; i1++) {
            s_v[i1]=pairing.getZr().newRandomElement().getImmutable();
        }
        System.out.println("Random vector matrix");
        Element[][] s_v_M=vectorToMatrix(s_v);
        printMatrix(s_v_M);


        Field groupZr=pairing.getZr();
        Element[] innerProduct =new Element[row];
        for (int i1 = 0; i1 < row; i1++) {
            Element rowProduct = groupZr.newElement();
            for (int j = 0; j < col; j++) {
                for (int k = 0; k < s_v_M[0].length; k++) {
                    rowProduct.add(s_v_M[j][k].mul(ShareArray[i1][j]).getImmutable());
                }
            }
            innerProduct[i1]=rowProduct;
        }
        System.out.println("Shared Matrix and Random Vector Inner Product");
        printMatrix(innerProduct);
        HashMap<String,Element> P=new HashMap<>();
        int si=innerProduct.length;
        for (Map.Entry<String,int[]> entry : result.entrySet()) {
            P.put(entry.getKey(),innerProduct[si-1]);
            System.out.println(entry.getKey()+":"+innerProduct[si-1]);
            si--;
        }

        //decrypt
        String[] attrs={"A","B","C","D"};
        int[][] userAttrArray = new int[attrs.length][];
        for (int i1 = 0; i1 < userAttrArray.length; i1++) {
            userAttrArray[i1]=result.get(attrs[i1]);
        }
        System.out.println("The mapping vector matrix and its transposition of user attributes in the shared matrix");
        printMatrix(userAttrArray);
        //Transposition
        int[][] userAttrArray_transpose = transpose(userAttrArray);
        printMatrix(userAttrArray_transpose);

        double[] CData = new double[userAttrArray_transpose.length];
        CData[0]=1;
        for (int i1 = 1; i1 < CData.length; i1++) {
            CData[i1]=0;
        }
//        printMatrix(CData);
        double[][] c=vectorToMatrix(CData);
        RealMatrix A = MatrixUtils.createRealMatrix(intToDoubleMatrix(userAttrArray_transpose));
        RealMatrix C = MatrixUtils.createRealMatrix(c);

        // Perform QR decomposition on matrix A
        QRDecomposition qr = new QRDecomposition(A);
        RealMatrix Q = qr.getQ();
        RealMatrix R = qr.getR();

        RealMatrix RInv = new QRDecomposition(R).getSolver().getInverse();

        RealMatrix B = RInv.multiply(Q.transpose()).multiply(C);


        double[][] w=B.getData();
        System.out.println("W matrix");
        printMatrix(w);

        Element[][] M_lab=new Element[attrs.length][];
        for (int j = 0; j < attrs.length; j++) {
            M_lab[j]=new Element[]{P.get(attrs[j])};
        }
        System.out.println("M_lab");
        printMatrix(M_lab);
        Element[][] M_lab_transpose=transpose(M_lab);
        System.out.println("M_lab_transpose");
        printMatrix(M_lab_transpose);

        Element[] innerProduct2 =new Element[w[0].length];
        for (int i1 = 0; i1 < M_lab_transpose.length; i1++) {
            Element rowProduct = groupZr.newElement();
            for (int j = 0; j < M_lab_transpose[0].length; j++) {
                for (int k = 0; k < w[0].length; k++) {
                    rowProduct.add(M_lab_transpose[i1][j].mul((int) w[j][k]).getImmutable());
                }
            }
            innerProduct2[i1]=rowProduct;
        }

        System.out.println(innerProduct2[0]);

    }
    public static int[][] transpose(int[][] matrix) {
        int res[][] = new int[matrix[0].length][matrix.length];

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j <  matrix[0].length; j++){
                res[j][i] = matrix[i][j];
            }
        }

        return res;

    }

    public static Element[][] transpose(Element[][] matrix) {
        Element res[][] = new Element[matrix[0].length][matrix.length];

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j <  matrix[0].length; j++){
                res[j][i] = matrix[i][j];
            }
        }

        return res;

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
    public static void printMatrix(double[][] matrix){
        for(int i = 0;i<matrix.length;i++) {
            for(int j =0;j<matrix[0].length;j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.println();
        }
        System.out.println();
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
    public static void printMatrix(Object[] matrix){
        for(int j =0;j<matrix.length;j++) {
            System.out.print(matrix[j]+"\n");
        }
        System.out.println();
    }
    public static void printMatrix(int[] matrix){
        for(int j =0;j<matrix.length;j++) {
            System.out.print(matrix[j]+"\n");
        }
        System.out.println();
    }
    public static double[][] vectorToMatrix(double[] matrix){
        double[][] res=new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            double[] a={matrix[i]};
            res[i]=a;
        }
        return res;
    }
    public static Element[][] vectorToMatrix(Element[] matrix){
        Element[][] res=new Element[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            Element[] a={matrix[i]};
            res[i]=a;
        }
        return res;
    }
    public static double[][] intToDoubleMatrix(int[][] matrix){
        double[][] res=new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                res[i][j]=matrix[i][j];
            }
        }
        return res;
    }


    public static HashMap<String,int[]> traverse(int counter,AccessControlTreeNode root) {
        HashMap<String,int[]> result = new HashMap<>();
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
                int[] paddedVector = padVector(vector,counter);
                AccessControlTreeNode left = node.getChildren().get(0);
                AccessControlTreeNode right = node.getChildren().get(1);
                left.setVector(concatVectors(new int[counter], new int[]{-1}));
                right.setVector(concatVectors(paddedVector, new int[]{1}));
//                node.setVector(concatVectors(paddedVector, new int[]{-1}));
                counter++;
            } else {
                // Leaf nodes
                result.put(node.getGateType(),padVector(vector,counter));
            }

            // Add child nodes to the queue
            for (AccessControlTreeNode child : node.getChildren()) {
                queue.offer(child);
            }
        }

        return result;
    }

    // Fill the end of the vector with 0 to the specified length
    public static int[] padVector(int[] vector,int counter) {
        int[] paddedVector = Arrays.copyOf(vector, counter);
        for (int i = vector.length; i < counter; i++) {
            paddedVector[i] = 0;
        }
        return paddedVector;
    }

    // Connect two vectors
    public static int[] concatVectors(int[] v1, int[] v2) {
        int[] result = new int[v1.length + v2.length];
        System.arraycopy(v1, 0, result, 0, v1.length);
        System.arraycopy(v2, 0, result, v1.length, v2.length);
        return result;
    }
}

class AccessControlTreeNode {
    private String gateType;
    private int[] vector;
    private List<AccessControlTreeNode> children;

    public AccessControlTreeNode(String gateType) {
        this.gateType = gateType;
        this.children = new ArrayList<>();
    }

    public String getGateType() {
        return gateType;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] vector) {
        this.vector = vector;
    }

    public List<AccessControlTreeNode> getChildren() {
        return children;
    }

    public void addChild(AccessControlTreeNode child) {
        children.add(child);
    }
}