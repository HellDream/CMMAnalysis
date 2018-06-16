package parser.nodes;

import java.util.ArrayList;

/**
 * Created by Yu on 2017/11/23.
 */
public class BlockStmtNode extends Node {
    private ArrayList<Node> stmtNodes = new ArrayList<>();

    public BlockStmtNode() {
        super();
        this.nodeType = BLOCK_STMT;
    }

    private boolean isStmtNodesEmpty = true;

    public boolean isStmtNodesEmpty() {
        return isStmtNodesEmpty;
    }
    public int getNodeType() {
        return nodeType;
    }

    public ArrayList<Node> getStmtNode() {
        return stmtNodes;
    }
    public void addStmtNode(Node node){
        isStmtNodesEmpty = false;
        stmtNodes.add(node);
    }
}
