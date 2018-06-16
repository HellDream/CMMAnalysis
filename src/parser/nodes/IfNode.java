package parser.nodes;

/**
 * Created by Yu on 2017/11/23.
 */
public class IfNode extends Node {
    private ExprNode exprNode;
    private Node blockStmtNode1 = null;
    private Node blockStmtNode2 = null;

    public IfNode() {
        super();
        this.nodeType = IF_STMT;

    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public Node getBlockStmtNode1() {
        return blockStmtNode1;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setExprNode(ExprNode exprNode) {
        this.exprNode = exprNode;
    }
    public void setBlockStmtNode1(Node blockStmtNode1) {
        this.blockStmtNode1 = blockStmtNode1;
    }

    public Node getBlockStmtNode2() {
        return blockStmtNode2;
    }

    public void setBlockStmtNode2(Node blockStmtNode2) {
        this.blockStmtNode2 = blockStmtNode2;
    }
}
