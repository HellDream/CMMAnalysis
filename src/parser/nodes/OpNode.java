package parser.nodes;

import lexer.Token;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by 79300 on 2017/11/23.
 */
public class OpNode extends Node {
    /**操作符的Token*/
    private Token token;
    /**左节点*/
    private Node left;
    /**右节点*/
    private Node right;

    public OpNode() {
        super();

    }

    public OpNode(Token token, Node left, Node right) {
        this.token = token;
        this.left = left;
        this.right = right;
        nodeType = Node.OP;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setValue(){
        if(calculatable()){
            if(left.dataType==Token.STRING_TEXT&&right.dataType==Token.STRING_TEXT){
                this.setValueString(left.getValueString()+right.getValueString());
            }
            else if(token.getType()==Token.ADD)
                plus();
            else if(token.getType()== Token.SUBTRACT)
                subtract();
            else if(token.getType()==Token.MUL)
                mul();
            else if(token.getType()==Token.DIV){
                if((Double)right.getValue()==0){
                    try {
                        throw new IOException();
                    } catch (IOException e) {
                        System.err.println("Divided by zero at "+right.toString());
                    }
                }else {
                    div();
                }
            }else if(token.getType()==Token.MORE)
                greaterThan();
            else if(token.getType()== Token.MOREEQ)
                greaterEQThan();
            else if(token.getType()==Token.LESS)
                lessThan();
            else if(token.getType()==Token.LESSEQ)
                lessEQThan();
            else if(token.getType()==Token.EQUAL)
                equal();
            else if(token.getType()==Token.NOTEQ)
                notEqual();


        }else {
            try {
                throw new IOException();
            } catch (IOException e) {
                System.err.println("Invalid operation at "+token.toString());
            }

        }
    }

    private void plus(){
        if(checkDataType(left.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)&&
                checkDataType(right.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)){
            this.setValueInt((Integer) left.getValue()+(Integer) right.getValue());
        }else {
            this.setValueDouble((Double) left.getValue()+(Double) right.getValue());
        }
    }
    private void subtract(){
        if(checkDataType(left.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)&&
                checkDataType(right.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)){
            this.setValueInt((Integer) left.getValue()-(Integer) right.getValue());
        }else {
            this.setValueDouble((Double) left.getValue()-(Double) right.getValue());
        }
    }
    private void mul(){
        if(checkDataType(left.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)&&
                checkDataType(right.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)){
            this.setValueInt((Integer) left.getValue()*(Integer) right.getValue());
        }else {
            this.setValueDouble((Double) left.getValue()*(Double) right.getValue());
        }
    }
    private void div(){
        if(checkDataType(left.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)&&
                checkDataType(right.dataType,Token.TRUE,Token.FALSE,Token.INT_NUMBER)){
            this.setValueInt((Integer) left.getValue()/(Integer) right.getValue());
        }else {
            this.setValueDouble((Double) left.getValue()/(Double) right.getValue());
        }
    }

    public void greaterThan(){
        if((Double) left.getValue()>(Double) right.getValue())
            this.setValueBool(1);
        else
            this.setValueBool(0);
    }
    public void equal(){
        if(Objects.equals((Double) left.getValue(), (Double) right.getValue()))
            this.setValueBool(1);
        else
            this.setValueBool(0);
    }
    public void lessThan(){
        if((Double) left.getValue()<(Double) right.getValue())
            this.setValueBool(1);
        else
            this.setValueBool(0);
    }
    public void greaterEQThan(){
        if((Double) left.getValue()>=(Double) right.getValue())
            this.setValueBool(1);
        else
            this.setValueBool(0);
    }
    public void lessEQThan(){
        if((Double) left.getValue()<=(Double) right.getValue())
            this.setValueBool(1);
        else
            this.setValueBool(0);
    }
    public void notEqual(){
        if(!Objects.equals((Double) left.getValue(), (Double) right.getValue()))
            this.setValueBool(1);
        else
            this.setValueBool(0);
    }

    public boolean checkDataType(int dataType,int ... type){
        for(int t:type){
            if(dataType==t)
                return true;
        }
        return false;
    }

    public boolean calculatable(){
        if((left.dataType==Token.STRING_TEXT&&right.dataType!=Token.STRING_TEXT)||
                (right.dataType==Token.STRING_TEXT&&left.dataType!=Token.STRING_TEXT)||
                (left.dataType==Token.STRING_TEXT&&right.dataType==Token.STRING_TEXT&&token.getType()!=Token.ADD)){
            return false;
        }
        return true;
    }

}
