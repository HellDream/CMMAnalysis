package semantic;

import exceptions.CMMException;
import lexer.Token;
import parser.nodes.FactorNode;

/**
 * Created by Yu on 2017/11/29.
 * 这个Value 类用来存放Expr得到的值
 */
public class Value {
    private boolean isString;
    private boolean isDouble;
    private boolean isBool;
    private boolean isInt;

    private int valueInt;
    private double valueDouble;
    private String valueString;
    private int bool;

    public boolean isString() {
        return isString;
    }

    public void setString(boolean string) {
        isString = string;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public void setDouble(boolean aDouble) {
        isDouble = aDouble;
    }

    public boolean isBool() {
        return isBool;
    }

    public void setBool(boolean bool) {
        isBool = bool;
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

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public int getBool() {
        return bool;
    }

    public void setBool(int bool) {
        this.bool = bool;
    }

    public void setValue(FactorNode factorNode){
        if(factorNode.getDataType()== Token.STRING_TEXT){
            setValueString(factorNode.getValueString().substring(1,factorNode.getValueString().length()-1));
            setString(true);
        }else if(factorNode.getDataType()==Token.INT_NUMBER){
            setValueInt(factorNode.getValueInt());
            setInt(true);
        }else if(factorNode.getDataType()==Token.DOUBLE_NUMBER){
            setValueDouble(factorNode.getValueDouble());
            setDouble(true);
        }else if(factorNode.getDataType()==Token.TRUE){
            setBool(factorNode.getValueBool());
            setBool(true);
        }else if(factorNode.getDataType()==Token.FALSE){
            setBool(factorNode.getValueBool());
            setBool(true);
        }
    }

    public boolean isInt() {
        return isInt;
    }

    public void setInt(boolean anInt) {
        isInt = anInt;
    }

    public double getOnlyValue(){
        if(isDouble())
            return valueDouble;
        if(isInt())
            return valueInt;
        if(isBool())
            return bool;
        return 0;
    }

    public void setValue(Variable variable, int index) throws CMMException {
        if(variable.isArray()){
            if(variable.getVariableType()== Variable.VariableType.DOUBLE_ARRAY){
                setValueDouble(variable.getDoubleValueIndexOf(index));
                setDouble(true);
            }
            else if(variable.getVariableType()== Variable.VariableType.INT_ARRAY){
                setValueInt(variable.getIntValueIndexOf(index));
                setInt(true);
            }
            else if(variable.getVariableType()== Variable.VariableType.BOOL_ARRAY){
                if(variable.getBoolValueIndexOf(index)){
                    setBool(1);
                    setBool(true);
                }else {
                    setBool(0);
                    setBool(true);
                }
            }
        }else {
            if(variable.getVariableType()== Variable.VariableType.DOUBLE){
                setValueDouble(variable.getDoubleValue());
                setDouble(true);
            } else if(variable.getVariableType()== Variable.VariableType.INT){
                setValueInt(variable.getIntValue());
                setInt(true);
            }
            else if(variable.getVariableType()== Variable.VariableType.BOOL){
                if(variable.isBoolValue()){
                    setBool(1);
                    setBool(true);
                }else {
                    setBool(0);
                    setBool(true);
                }
            }else if(variable.getVariableType()== Variable.VariableType.STRING){
                setValueString(variable.getStringValue());
                setString(true);
            }
        }
    }

    public static Value calculate(Value left, Value right, int tokenType){
        if(calculatable(left, right, tokenType)){
            Value value = new Value();
            if(tokenType==Token.ADD){
                value = Util.plus(left,right);
            }else if(tokenType==Token.SUBTRACT){
                value = Util.subtract(left,right);
            }else if(tokenType==Token.MUL){
                value = Util.mul(left,right);
            }else if(tokenType==Token.DIV){
                value = Util.div(left,right);
            }else if(tokenType==Token.MORE){
                value = Util.moreThan(left,right);
            }else if(tokenType==Token.MOREEQ){
                value = Util.moreEq(left,right);
            }else if(tokenType==Token.LESS){
                value = Util.lessThan(left,right);
            }else if(tokenType==Token.LESSEQ){
                value = Util.lessEq(left,right);
            }else if(tokenType==Token.EQUAL){
                value = Util.Equal(left,right);
            }else if(tokenType==Token.NOTEQ){
                value = Util.notEqual(left,right);
            }
            return value;
        }else {
            return null;
        }
    }

    private static boolean calculatable(Value left, Value right, int tokenType) {
        if((left.isString()&&!right.isString())||
                (right.isString()&&!left.isString())||
                (left.isString()&&right.isString()&&tokenType!=Token.ADD)){
            return false;
        }
        return true;

    }

    @Override
    public String toString() {
        String s="";
        if(isString()){
            s=this.valueString;
        }else if(isBool){
            s= String.valueOf(this.bool);
        }else if(isInt()){
            s=String.valueOf(valueInt);
        }else if(isDouble()){
            s=String.valueOf(valueDouble);
        }
        return s;
    }
}
