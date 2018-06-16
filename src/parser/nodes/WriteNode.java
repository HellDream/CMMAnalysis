package parser.nodes;

import lexer.Token;

/**
 * Created by 79300 on 2017/11/23.
 */
public class WriteNode extends Node {
    ExprNode exprNode;
    private Token stringConst;

    public WriteNode() {
        super();
        this.nodeType = WRITE_STMT;

    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public void setExprNode(ExprNode exprNode) {
        this.exprNode = exprNode;
    }
    public Token getStringConst() {
        return stringConst;
    }

    public void setStringConst(Token stringConst) {
        this.stringConst = stringConst;
    }
}
