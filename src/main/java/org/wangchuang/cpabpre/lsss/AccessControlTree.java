package org.wangchuang.cpabpre.lsss;

import org.wangchuang.cpabpre.attribute.Attribute;
import org.wangchuang.cpabpre.parameter.PublicKey;
import org.wangchuang.cpabpre.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class AccessControlTree {
    private List<Attribute> attributes;

    public AccessControlTree(){
        this.attributes=new ArrayList<>();
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public AccessControlTreeNode buildTree(int numOfAttributes, PublicKey pk) {

        AccessControlTreeNode root = new AccessControlTreeNode("AND");
        buildSubtree(root, numOfAttributes,pk);
        return root;
    }
    public AccessControlTreeNode buildTree2(int numOfAttributes, PublicKey pk) {

        AccessControlTreeNode root = new AccessControlTreeNode("AND");
        buildSubtree2(root, numOfAttributes,pk);
        return root;
    }

    private void buildSubtree(AccessControlTreeNode node, int numOfAttributes,PublicKey pk) {
        if (numOfAttributes == 1) {
            Attribute left = new Attribute(Util.h256_f_string(String.valueOf(Math.random())), pk,1);
            Attribute right = new Attribute(Util.h256_f_string(String.valueOf(Math.random())), pk,1);
            this.attributes.add(left);
            this.attributes.add(right);
            node.addChild(new AccessControlTreeNode(left));
            node.addChild(new AccessControlTreeNode(right));

        } else {
            AccessControlTreeNode leftChild = new AccessControlTreeNode("AND");
            node.addChild(leftChild);
            buildSubtree(leftChild, numOfAttributes / 2,pk);
            AccessControlTreeNode rightChild = new AccessControlTreeNode("AND");
            node.addChild(rightChild);
            buildSubtree(rightChild, numOfAttributes / 2,pk);
        }
    }

    private void buildSubtree2(AccessControlTreeNode node, int numOfAttributes,PublicKey pk) {
        if(numOfAttributes<3){
            Attribute a = new Attribute(Util.h256_f_string(String.valueOf(Math.random())), pk,1);
            Attribute b = new Attribute(Util.h256_f_string(String.valueOf(Math.random())), pk,1);
            node.addChild(new AccessControlTreeNode(a));
            node.addChild(new AccessControlTreeNode(b));
            this.attributes.add(a);
            this.attributes.add(b);

        }else {
            AccessControlTreeNode leftChild = new AccessControlTreeNode("AND");
            Attribute right = new Attribute(Util.h256_f_string(String.valueOf(Math.random())), pk,1);
            AccessControlTreeNode rightChild = new AccessControlTreeNode(right);
            node.addChild(leftChild);
            node.addChild(rightChild);
            this.attributes.add(right);
            buildSubtree2(leftChild,numOfAttributes-1,pk);
        }
    }
}
