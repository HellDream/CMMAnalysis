package parser.nodes;

import lexer.Token;

import java.util.HashMap;

/**
 * Created by 79300 on 2017/11/23.
 */
public class DeclNode extends Node {
    /**存储声明变量的类型*/
    private int tokenType;
    /**哈希表存储前一个为Value， 后一个为expr*/
//    private Hashtable<Node,Node> hashMap;
    private HashMap<ValueNode, ExprNode> hashMap;
    public DeclNode(){
        super();
        hashMap = new HashMap<>();
        this.nodeType = DECLARE_STMT;

    }
    public DeclNode(Token token) {
        this.tokenType = token.getType();
    }
    public void put(ValueNode value, ExprNode expr){
        hashMap.put(value,expr);
    }
    public ExprNode getValueNode(ValueNode value){
        return hashMap.get(value);
    }

    public void setTokenType(int tokenType) {
        this.tokenType = tokenType;
    }

    public int getNodeType() {
        return nodeType;
    }

    public int getTokenType() {
        return tokenType;
    }

    public HashMap<ValueNode, ExprNode> getHashMap() {
        return hashMap;
    }
}
