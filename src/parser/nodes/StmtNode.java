package parser.nodes;

/**
 * Created by Yu on 2017/11/23.
 */
public class StmtNode extends Node {
    public StmtNode(){
        super();
        this.nodeType = STMT;

    }

    public int getNodeType() {
        return nodeType;
    }
}
