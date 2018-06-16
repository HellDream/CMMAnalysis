package parser.nodes;

/**
 * Created by Yu on 2017/11/23.
 */
public class WhileNode extends Node {
    private ExprNode exprNode;
    private Node blockStmtNode;
    private Node brkNode;
    private boolean hasbreak = false;
    public WhileNode() {
        super();
        this.nodeType = WHILE_STMT;
    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public void setExprNode(ExprNode exprNode) {
        this.exprNode = exprNode;
    }

    public Node getBlockStmtNode() {
        return blockStmtNode;
    }

    public void setBlockStmtNode(Node blockStmtNode) {
        this.blockStmtNode = blockStmtNode;
    }

    public int getNodeType() {
        return nodeType;
    }

    public Node getBrkNode() {
        return brkNode;
    }

    public void setBrkNode(Node brkNode) {
        this.brkNode = brkNode;
        hasbreak = true;
    }

    public boolean isHasbreak() {
        return hasbreak;
    }
}
