package parser.nodes;

import lexer.Token;

import java.util.ArrayList;

/**
 * Created by 79300 on 2017/11/23.
 */
public class AssignStmtNode extends Node {
    private ValueNode valueNode;
    private ExprNode exprNode;
    //函数赋值用
    private boolean isAssignFunction = false;
    private Token functionNameToken;
    private ArrayList<ValueNode> valueNodes =new ArrayList<>();

    public AssignStmtNode() {
        super();
        this.nodeType = ASSIGN_STMT;
    }

    public ValueNode getValueNode() {
        return valueNode;
    }

    public void setValueNode(ValueNode valueNode) {
        this.valueNode = valueNode;
    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public void setExprNode(ExprNode exprNode) {
        this.exprNode = exprNode;
    }

    public int getNodeType() {
        return nodeType;
    }

    public boolean isAssignFunction() {
        return isAssignFunction;
    }

    public void setAssignFunction(boolean assignFunction) {
        isAssignFunction = assignFunction;
    }

    public Token getFunctionNameToken() {
        return functionNameToken;
    }

    public void setFunctionNameToken(Token functionNameToken) {
        this.functionNameToken = functionNameToken;
    }

    public ArrayList<ValueNode> getValueNodes() {
        return valueNodes;
    }

    public void addValueNode(ValueNode valueNode){
        this.valueNodes.add(valueNode);
    }

}
