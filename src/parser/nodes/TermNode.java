package parser.nodes;

import lexer.Token;

/**
 * Created by 79300 on 2017/11/26.
 */
public class TermNode extends Node {
    /**factor node or op node*/
    private Node factorOrOpNode;

    public TermNode() {
        super();
        this.nodeType = TERM;

    }

    public int getNodeType() {
        return nodeType;
    }

    public void setValue(){
        this.dataType = factorOrOpNode.dataType;
        if(factorOrOpNode.dataType == Token.INT_NUMBER){
            this.setValueInt(factorOrOpNode.getValueInt());
        }else if(factorOrOpNode.dataType==Token.DOUBLE_NUMBER){
            this.setValueDouble(factorOrOpNode.getValueDouble());
        }else if(factorOrOpNode.dataType==Token.TRUE){
            this.setValueBool(factorOrOpNode.getValueBool());
        }else if(factorOrOpNode.dataType==Token.FALSE){
            this.setValueBool(factorOrOpNode.getValueBool());
        }else if(factorOrOpNode.dataType==Token.STRING_TEXT){
            this.setValueString(factorOrOpNode.getValueString());
        }
    }

    public Node getFactorOrOpNode() {
        return factorOrOpNode;
    }

    public void setFactorOrOpNode(Node factorOrOpNode) {
        this.factorOrOpNode = factorOrOpNode;
    }
}
