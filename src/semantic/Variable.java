package semantic;

import exceptions.CMMException;
import exceptions.Error;
import parser.nodes.ValueNode;

import static exceptions.Error.INDEX_OUT_OF_BOUND;

/**
 * Created by Yu on 2017/11/28.
 */
public class Variable {
    /**变量名*/
    private String valueName;
    /**变量可能的值*/
    private int intValue;
    private double doubleValue;
    private boolean boolValue;
    private String stringValue;
    private int[] intArray;
    private double[] doubleArray;
    private boolean[] boolArray;
    /**存储数组大小*/
    private int arrayLength;
    private boolean isArray=false;

    private int lineNumber;
    private int position;

    public boolean isArray() {
        return isArray;
    }

    public void setValue(Value value, ValueNode valueNode) throws CMMException {
        if(variableType==VariableType.INT&&value.isInt()){
            setIntValue(value.getValueInt());
        }else if(variableType==VariableType.DOUBLE){
            setDoubleValue(value.getOnlyValue());
        }else if(variableType==VariableType.STRING&&value.isString()){
            setStringValue(value.getValueString());
        }else if(variableType==VariableType.BOOL&&value.isBool()){
            if(value.getBool()==1)
                setBoolValue(true);
            else
                setBoolValue(false);
        }else {
            throw new CMMException(Error.VARIABLE_ASSIGN_ERROR,Error.VARIABLE_ASSIGN_ERROR+": Variable cannot be assigned. LineNo: "+valueNode.getLineNumber(),
                    valueNode.getLineNumber(),valueNode.getPosition());

        }
    }


    /**变量类型*/
    public enum VariableType {
        INT,DOUBLE,BOOL,INT_ARRAY,DOUBLE_ARRAY, BOOL_ARRAY,STRING
    }
    /**存储变量类型*/
    private VariableType variableType;
    /**存储变量所在层次*/
    private int scope;

    public Variable(String valueName, VariableType variableType) {
        this.valueName = valueName;
        this.variableType = variableType;
    }

    public Variable() {
    }

    /**Getter and Setter*/
    public String getValueName() {
        return valueName;
    }
    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public boolean isBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public double[] getDoubleArray() {
        return doubleArray;
    }

    public void setDoubleArray(double[] doubleArray) {
        this.doubleArray = doubleArray;
    }

    public boolean[] getBoolArray() {
        return boolArray;
    }

    public void setBoolArray(boolean[] boolArray) {
        this.boolArray = boolArray;
    }

    public int getArrayLength() {
        return arrayLength;
    }
    /**设置数组长度并初始化*/
    public void setArrayLength(int arrayLength) {
        this.arrayLength = arrayLength;
        this.isArray=true;
        if(getVariableType()==VariableType.BOOL_ARRAY){
            boolArray = new boolean[arrayLength];
            for(int i=0;i<arrayLength;i++){
                boolArray[i]=true;
            }
        }
        else if(getVariableType()==VariableType.INT_ARRAY){
            intArray = new int[arrayLength];
            for(int i=0;i<arrayLength;i++){
                intArray[i]=0;
            }
        }
        else if(getVariableType()==VariableType.DOUBLE_ARRAY){
            doubleArray = new double[arrayLength];
            for(int i=0;i<arrayLength;i++){
                doubleArray[i]=0;
            }
        }
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
        if(variableType==VariableType.BOOL_ARRAY||variableType==VariableType.DOUBLE_ARRAY||variableType==VariableType.INT_ARRAY)
            this.isArray=true;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }
    /**获取数组index的值*/
    public int getIntValueIndexOf(int index) throws CMMException {
        if(!isOutOfBound(index)){
            return intArray[index];
        }else {
            try {
                throw new ArrayIndexOutOfBoundsException();
            }catch (ArrayIndexOutOfBoundsException a){
                throw new CMMException(INDEX_OUT_OF_BOUND,INDEX_OUT_OF_BOUND+": index out of bound at "+this.getValueName(),this.getLineNumber(),this.getPosition());
            }
        }
    }

    public void setArrayIndexOf(int index, Value value, ValueNode valueNode) throws CMMException {
        if(!isOutOfBound(index)) {
            if (variableType == VariableType.INT_ARRAY && value.isInt()) {
                intArray[index] = value.getValueInt();
            } else if (variableType == VariableType.DOUBLE_ARRAY) {
                doubleArray[index] = value.getOnlyValue();
            } else if (variableType == VariableType.BOOL && value.isBool()) {
                boolArray[index] = value.getBool() == 1;
            }else {
                throw new CMMException(Error.VARIABLE_ASSIGN_ERROR,
                        Error.VARIABLE_ASSIGN_ERROR+": Variable cannot be assigned. LineNo: "+valueNode.getLineNumber(),
                        valueNode.getLineNumber(),valueNode.getPosition());
            }
        }else {
            try {
                throw new ArrayIndexOutOfBoundsException();
            }catch (ArrayIndexOutOfBoundsException a){
                throw new CMMException(INDEX_OUT_OF_BOUND,INDEX_OUT_OF_BOUND+": index out of bound at "+this.getValueName(),this.getLineNumber(),this.getPosition());
            }
        }
    }

    public double getDoubleValueIndexOf(int index) throws CMMException {
        if(!isOutOfBound(index)){
            return doubleArray[index];
        }else {
            try {
                throw new ArrayIndexOutOfBoundsException();
            }catch (ArrayIndexOutOfBoundsException a){
                throw new CMMException(INDEX_OUT_OF_BOUND,INDEX_OUT_OF_BOUND+": index out of bound at "+this.getValueName(),this.getLineNumber(),this.getPosition());
            }
        }
    }
    public boolean getBoolValueIndexOf(int index) throws CMMException {
        if(!isOutOfBound(index)){
            return boolArray[index];
        }else {
            try {
                throw new ArrayIndexOutOfBoundsException();
            }catch (ArrayIndexOutOfBoundsException a){
                throw new CMMException(INDEX_OUT_OF_BOUND,INDEX_OUT_OF_BOUND+": index out of bound at "+this.getValueName(),this.getLineNumber(),this.getPosition());
            }
        }
    }

    /**是否越界
     * 越界返回true
     * 否则返回false*/
    public boolean isOutOfBound(int index){
        return !(index >= 0 && index < arrayLength);
    }

    @Override
    public String toString() {
        return "Variable:"+ this.getValueName();
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
}
