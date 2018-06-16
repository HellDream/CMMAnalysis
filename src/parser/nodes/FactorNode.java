package parser.nodes;

import lexer.Token;

/**
 * Created by 79300 on 2017/11/26.
 */
public class FactorNode extends Node {
    /** VALUE| INTCONSTANT | DOUBLECONSTANT|TRUE|FALSE*/
    private ValueNode valueNode;
    private ExprNode exprNode = null;


    public FactorNode() {
        super();
        this.nodeType = FACTOR;

    }

    public int getNodeType() {
        return nodeType;
    }

    public ValueNode getValueNode() {
        return valueNode;
    }

    public void setNode(ValueNode node) {
        this.valueNode = node;
        this.dataType = valueNode.dataType;
        if(valueNode.dataType == Token.INT_NUMBER){
            this.setValueInt(valueNode.getValueInt());
        }else if(valueNode.dataType==Token.DOUBLE_NUMBER){
            this.setValueDouble(valueNode.getValueDouble());
        }else if(valueNode.dataType==Token.TRUE){
            this.setValueBool(valueNode.getValueBool());
        }else if(valueNode.dataType==Token.FALSE){
            this.setValueBool(valueNode.getValueBool());
        }else if(valueNode.dataType==Token.STRING_TEXT){
            this.setValueString(valueNode.getValueString());
        }
    }

    public void setValue(Node endTokenNode){
        int tokenType = endTokenNode.getToken().getType();
        this.dataType = tokenType;
        if(tokenType==Token.INT_NUMBER){
            this.valueInt = Integer.parseInt(endTokenNode.getToken().getValue());
        }else if(tokenType==Token.DOUBLE_NUMBER){
            this.valueDouble = Double.parseDouble(endTokenNode.getToken().getValue());
        }else if(tokenType== Token.TRUE){
            this.valueBool = 1;
        }else if(tokenType==Token.FALSE){
            this.valueBool = 0;
        }else if(tokenType==Token.STRING_TEXT){
            this.valueString=endTokenNode.getToken().getValue();
        }
    }


    public void setValue(ValueNode valueNode, Object value){
        if(valueNode.getType()==Token.INT){
            valueInt = (Integer) value;
        }else if(valueNode.getType()==Token.DOUBLE){
            valueDouble  =(Double) value;
        }
    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public void setExprNode(ExprNode exprNode) {
        this.exprNode = exprNode;
        this.dataType = exprNode.dataType;

        if(exprNode.dataType == Token.INT_NUMBER){
            this.setValueInt(exprNode.getValueInt());
        }else if(exprNode.dataType==Token.DOUBLE_NUMBER){
            this.setValueDouble(exprNode.getValueDouble());
        }else if(exprNode.dataType==Token.TRUE){
            this.setValueBool(exprNode.getValueBool());
        }else if(exprNode.dataType==Token.FALSE){
            this.setValueBool(exprNode.getValueBool());
        }else if(exprNode.dataType==Token.STRING_TEXT){
            this.setValueString(exprNode.getValueString());
        }
    }

}
