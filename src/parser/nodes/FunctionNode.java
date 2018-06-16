package parser.nodes;

import lexer.Token;

import java.util.ArrayList;

/**
 * Created by Yu on 2017/12/4.
 */
public class FunctionNode extends Node {
    /**函数类型的token
     * void int 等*/
    private Token token;
    private int type;
    /**形参*/
    private BlockStmtNode blockStmtNode;
    private ArrayList<ValueNode> valueNodes = new ArrayList<>();
    /**函数名*/
    private Token functionNameToken;
    private String name;

    public FunctionNode() {
        super();
        this.nodeType=FUNCTION_STMT;
    }

    @Override
    public Token getToken() {
        return token;
    }

    public void setFunctionNameToken(Token token){
        this.functionNameToken = token;
        this.name = token.getValue();
    }
    public void setToken(Token token) {
        this.token = token;
        this.type = token.getType();
    }

    public int getType() {
        return type;
    }

    public ArrayList<ValueNode> getValueNodes() {
        return valueNodes;
    }

    public void setValueNodes(ArrayList<ValueNode> valueNodes) {
        this.valueNodes = valueNodes;
    }

    public void addValueNode(ValueNode valueNode){
        valueNodes.add(valueNode);
    }

    public BlockStmtNode getBlockStmtNode() {
        return blockStmtNode;
    }

    public void setBlockStmtNode(BlockStmtNode blockStmtNode) {
        this.blockStmtNode = blockStmtNode;
    }

    public Token getFunctionNameToken() {
        return functionNameToken;
    }

    public String getName() {
        return name;
    }
}
