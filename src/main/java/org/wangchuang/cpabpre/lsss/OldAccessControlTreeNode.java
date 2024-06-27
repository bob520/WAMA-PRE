package org.wangchuang.cpabpre.lsss;

import it.unisa.dia.gas.jpbc.Element;
import org.wangchuang.cpabpre.attribute.Attribute;

import java.util.ArrayList;
import java.util.List;

public class OldAccessControlTreeNode {
    private String gateType;
    private Attribute attribute;
    private Element[] vector;

    private List<OldAccessControlTreeNode> children;



    public OldAccessControlTreeNode(String gateType) {
        this.gateType = gateType;
        this.children = new ArrayList<>();
        this.attribute=null;
    }
    public OldAccessControlTreeNode(Attribute attribute) {
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

    public Element[] getVector() {
        return vector;
    }

    public void setVector(Element[] vector) {
        this.vector = vector;
    }

    public List<OldAccessControlTreeNode> getChildren() {
        return children;
    }

    public void addChild(OldAccessControlTreeNode child) {
        children.add(child);
    }
}