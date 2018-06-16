package parser.nodes;

/**
 * Created by Yu on 2017/12/4.
 */
public class ReturnNode extends Node{
    ExprNode exprNode;

    public ReturnNode() {
        super();
        this.nodeType=RETURN_STMT;
    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public void setValueNode(ExprNode exprNode) {
        this.exprNode = exprNode;
    }
}
