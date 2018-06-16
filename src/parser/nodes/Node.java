package parser.nodes;

import lexer.Token;

import java.util.ArrayList;

/**
 * Created by 79300 on 2017/11/6.
 */
public class Node {
    //定义Node类型
    /**非终结符*/
    public static final int PROGRAM=1;
    public static final int STMT=2;
    public static final int DECLARE_STMT=3;
    public static final int IF_STMT=4;
    public static final int WHILE_STMT=5;
    public static final int BREAK_STMT=6;
    public static final int ASSIGN_STMT=7;
    public static final int READ_STMT=8;
    public static final int WRITE_STMT=9;
    public static final int BLOCK_STMT=10;
    public static final int FUNCTION_STMT=11;
    public static final int OP=12;
    public static final int RETURN_STMT=13;
    public static final int EXPR=16;
    public static final int VALUE=17;
    public static final int TERM=18;
    public static final int FACTOR=19;
    /**终结符*/
    public static final int ENDTOKEN=20;

    public Token getToken() {
        return token;
    }


    /**用于存放当前终结符，非终结符时为空*/
    private Token token;
    /**非终结符时存放子结点*/
    private ArrayList<Node> children = new ArrayList<>();
    /**存放结点类型*/
    protected int nodeType;


    /**存放计算出的结点的属性*/
    /**NTCONS DOUBLECONST */
    protected int dataType;
    protected int valueInt;
    protected double valueDouble;
    protected int valueBool;
    protected String valueString;
    protected int lineNumber;
    protected int position;
    public int getDataType() {
        return dataType;
    }

    public Object getValue(){
        switch (this.dataType){
            case Token.STRING_TEXT:
                return this.valueString;
            case Token.TRUE:
            case Token.FALSE:
                return this.valueBool;
            case Token.DOUBLE_NUMBER:
                return this.valueDouble;
            case Token.INT_NUMBER:
                return this.valueInt;
            default:
                return null;
        }
    }

    public int getValueInt() {
        return valueInt;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public double getValueDouble() {
        return valueDouble;
    }

    public void setValueDouble(double valueDouble) {
        this.valueDouble = valueDouble;
    }

    public int getValueBool() {
        return valueBool;
    }

    public void setValueBool(int valueBool) {
        this.valueBool = valueBool;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Node() {
    }

    /**非终结符类型的构造函数*/
    public Node(int nodeType){
        this.nodeType=nodeType;
        token = null;
    }
    /**ENDTOKEN类型的构造函数*/
    public Node(Token endToken){
        this.token=endToken;
        this.nodeType=ENDTOKEN;
        this.lineNumber=endToken.getLineNo();
        this.position=endToken.getPosition();
    }

    /** getter */
    public ArrayList<Node> getChildren() {
        return children;
    }

    /**获取孩子中的其中一个节点*/
    public Node getChildren(int i){
        return this.children.get(i);
    }

    public String getType(int i){
        switch (i){
            case 1:
                return "PROGRAM";
            case 2:
                return "STMT";
            case 3:
                return "DECLARE_STMT";
            case 4:
                return "IF_STMT";
            case 5:
                return "WHILE_STMT";
            case 6:
                return "BREAK_STMT";
            case 7:
                return "ASSIGN_STMT";
            case 8:
                return "READ_STMT";
            case 9:
                return "WRITE_STMT";
            case 10:
                return "BLOCK_STMT";
            case 11:
                return "FUNCTION_STMT";
            case 12:
                return "OPERATION";
            case 13:
                return "RETURN_STMT";
            case 14:
                return "";
            case 15:
                return "";
            case 16:
                return "EXPR";
            case 17:
                return "VALUE";
            case 18:
                return "TERM";
            case 19:
                return "FACTOR";
            case 20:
                return "ENDTOKEN";
        }
        return "";
    }

    @Override
    public String toString() {
        if(token==null)
            return "currentNode: type-"+getType(nodeType);
        return "currentNode: type-END "+token.toString();
    }


    /**添加子结点*/
    public void add(Node node){
        children.add(node);
    }

    public int getNodeType() {
        return nodeType;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setLineNumberAndPosition(int lineNumber, int position){
        this.lineNumber=lineNumber;
        this.position=position;
    }
}


