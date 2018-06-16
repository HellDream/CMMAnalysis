package parser.nodes;

import lexer.Token;

/**
 * Created by 79300 on 2017/11/23.
 */
public class ReadNode extends Node{
    private ValueNode valueNode;
    private Token stringConst;
    public ReadNode() {
        super();
        this.nodeType = READ_STMT;

    }

    public ValueNode getValueNode() {
        return valueNode;
    }

    public void setValueNode(ValueNode valueNode) {
        this.valueNode = valueNode;
    }

    public Token getStringConst() {
        return stringConst;
    }

    public void setStringConst(Token stringConst) {
        this.stringConst = stringConst;
    }
}
