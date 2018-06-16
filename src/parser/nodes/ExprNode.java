package parser.nodes;

import lexer.Token;

/**
 * Created by 79300 on 2017/11/23.
 */
public class ExprNode extends Node {
    /**如果只有一个term 那么着这个节点就是TERM，否则就是OpNode*/
    private Node termNodeOrOpNode;
    /**如果有比较大小的话，存下比较的节点*/
    private Node compareNode;
    private boolean hasCompare = false;
    public ExprNode() {
        super();
        this.nodeType = EXPR;
    }

    public int getNodeType() {
        return nodeType;
    }

    public Node getTermNodeOrOpNode() {
        return termNodeOrOpNode;
    }

    public Node getCompareNode() {
        return compareNode;
    }

    public void setCompareNode(Node compareNode) {
        this.compareNode = compareNode;
    }

    public void setTermNodeOrOpNode(Node termNodeOrOpNode) {
        this.termNodeOrOpNode = termNodeOrOpNode;
    }

/*
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("currentNode: type-EXPR:\n");
        if(termNodeOrOpNode!=null)
            stringBuilder.append(termNodeOrOpNode.toString());
        if(compareNode!=null)
            stringBuilder.append(compareNode.toString());
        return stringBuilder.toString();
    }
*/

    public void setValue(){
        this.dataType = termNodeOrOpNode.dataType;
        if(termNodeOrOpNode.dataType == Token.INT_NUMBER){
            this.setValueInt(termNodeOrOpNode.getValueInt());
        }else if(termNodeOrOpNode.dataType==Token.DOUBLE_NUMBER){
            this.setValueDouble(termNodeOrOpNode.getValueDouble());
        }else if(termNodeOrOpNode.dataType==Token.TRUE){
            this.setValueBool(termNodeOrOpNode.getValueBool());
        }else if(termNodeOrOpNode.dataType==Token.FALSE){
            this.setValueBool(termNodeOrOpNode.getValueBool());
        }else if(termNodeOrOpNode.dataType==Token.STRING_TEXT){
            this.setValueString(termNodeOrOpNode.getValueString());
        }
    }

    public boolean isHasCompare() {
        return hasCompare;
    }

    public void setHasCompare(boolean hasCompare) {
        this.hasCompare = hasCompare;
    }
}
