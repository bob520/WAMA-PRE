package org.wangchuang.cpabpre.lsss;

import it.unisa.dia.gas.jpbc.Element;
import org.wangchuang.cpabpre.attribute.Attribute;

import java.util.ArrayList;
import java.util.List;

public class AccessControlTreeNode {
    private String gateType;
    private Attribute attribute;
    private int[] vector;

    private List<AccessControlTreeNode> children;



    public AccessControlTreeNode(String gateType) {
        this.gateType = gateType;
        this.children = new ArrayList<>();
        this.attribute=null;
    }
    public AccessControlTreeNode(Attribute attribute) {
        this.gateType = "";
        this.children = new ArrayList<>();
        this.attribute=attribute;
    }
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
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