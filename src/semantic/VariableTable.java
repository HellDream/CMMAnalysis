package semantic;

import java.util.ArrayList;

/**
 * Created by Yu on 2017/11/28.
 */
public class VariableTable {
    private ArrayList<Variable> list=new ArrayList<>();
    private ArrayList<VariableTable> functionTable = new ArrayList<>();
    private boolean isInFunction = false;
    /**添加变量*/
    public void addVariable(Variable variable, int index){
        if(index<0){
            list.add(variable);
        }
        else {
            VariableTable funcTable = functionTable.get(index);
            funcTable.addVariable(variable,-1);
        }
    }
    /**添加函数表*/
    public void addFunctionTable(VariableTable variableTable){
        this.functionTable.add(variableTable);
    }

    /**删除变量*/
    public void remove(Variable variable,int index){
        if(index<0) {
            list.remove(variable);
        }else {
            VariableTable funcTable = functionTable.get(index);
            funcTable.remove(variable,-1);
        }
    }
    /**表的大小*/
    public int size(){
        return list.size();
    }
    /**是否包含变量*/
    public boolean contains(Variable variable, int funcIndex){
        if(funcIndex>=0){
            VariableTable funcTable = this.functionTable.get(funcIndex);
            for(Variable tmpVariable: funcTable.getList()){
                if (tmpVariable.getValueName().equals(variable.getValueName()))/* &&
                    tmpVariable.getScope() <= variable.getScope()) */{
                    return true;
                }
            }
//            return false;
        }
        for (Variable tmpVariable : list) {
            if (tmpVariable.getValueName().equals(variable.getValueName()))/* &&
                    tmpVariable.getScope() <= variable.getScope()) */{
                return true;
            }
        }
        return false;
    }

    public boolean funcContains(Variable variable, int funcIndex){
        VariableTable funcTable = this.functionTable.get(funcIndex);
        for(Variable tmpVariable: funcTable.getList()){
            if (tmpVariable.getValueName().equals(variable.getValueName()))/* &&
                    tmpVariable.getScope() <= variable.getScope()) */{
                return true;
            }
        }
            return false;
    }

    /**获取变量*/
    public Variable getVariable(Variable variable, int funcIndex){
        if(isInFunction&&funcIndex>=0){
            VariableTable variableTable = functionTable.get(funcIndex);
            for (Variable tmpVariable : variableTable.getList()) {
                if (tmpVariable.getValueName().equals(variable.getValueName())/* &&
                    tmpVariable.getScope() <= variable.getScope()*/) {
                    return tmpVariable;
                }
            }
        }
        for (Variable tmpVariable : list) {
            if (tmpVariable.getValueName().equals(variable.getValueName())/* &&
                    tmpVariable.getScope() <= variable.getScope()*/) {
                return tmpVariable;
            }
        }
        return null;
    }

    public ArrayList<Variable> getList() {
        return list;
    }

    public boolean isInFunction() {
        return isInFunction;
    }
    public VariableTable getFunctionTable(int i){
        return this.functionTable.get(i);
    }
    public void setInFunction(boolean inFunction) {
        isInFunction = inFunction;
    }
    public void removeFunctionTable(int index){
        this.functionTable.remove(index);
    }
}
