package parser.nodes;

import lexer.Token;

import java.util.ArrayList;

/**
 * Created by Yu on 2017/11/23.
 */
public class ValueNode extends Node {

    @Override
    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
        this.lineNumber = token.getLineNo();
        this.position = token.getPosition();
    }

    /**VALUE 节点的名称 即变量名*/
    private Token token;
    private String value;

    /**存储对象类型 如int，double，等*/
    private int type;
    /** 给array用*/
    private boolean isArray = false;
    private int arrayLength;

    /* 可能是ID*/
    private ValueNode valueNode;
    private boolean hasValueNode;
    private int[] arrayInt;
    private double[] arrayDouble;
    private boolean[] arrayBool;
    /* 可能是调用函数的ValueNode */
    private boolean isCallFunctionNode = false;
    //存储传递参数的节点
    private ArrayList<ExprNode> exprNodes = new ArrayList<>();


    public ValueNode(Token token,int type) {
        super();
        this.token = token;
        value = token.getValue();
        this.type = type;
        this.nodeType = VALUE;

    }
    public ValueNode(int type) {
        super();
        this.type = type;
        this.nodeType = VALUE;
    }
    public void setArrayLength(int arrayLength) {
        isArray=true;
        this.arrayLength = arrayLength;
        initArray();
    }

    private void initArray(){
        if(type==Token.INT)
            arrayInt = new int[arrayLength];
        else if(type==Token.DOUBLE)
            arrayDouble = new double[arrayLength];
        else if(type==Token.BOOL)
            arrayBool = new boolean[arrayLength];
    }

    public int getNodeType() {
        return nodeType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getArrayLength() {
        return arrayLength;
    }

    public int[] getArrayInt() {
        return arrayInt;
    }

    public void setArrayInt(int[] arrayInt) {
        this.arrayInt = arrayInt;
    }

    public double[] getArrayDouble() {
        return arrayDouble;
    }

    public void setArrayDouble(double[] arrayDouble) {
        this.arrayDouble = arrayDouble;
    }

    public boolean[] getArrayBool() {
        return arrayBool;
    }

    public void setArrayBool(boolean[] arrayBool) {
        this.arrayBool = arrayBool;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public ValueNode getValueNode() {
        return valueNode;
    }

    public void setValueNode(ValueNode valueNode) {
        this.valueNode = valueNode;
        this.hasValueNode=true;
    }

    public boolean isHasValueNode() {
        return hasValueNode;
    }

    public boolean isCallFunctionNode() {
        return isCallFunctionNode;
    }

    public void setCallFunctionNode(boolean callFunctionNode) {
        isCallFunctionNode = callFunctionNode;
    }

    public ArrayList<ExprNode> getExprNodes() {
        return exprNodes;
    }

    public void addExprNode(ExprNode exprNode){
        this.exprNodes.add(exprNode);
    }


}
