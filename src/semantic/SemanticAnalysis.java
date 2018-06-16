package semantic;
import exceptions.CMMException;
import exceptions.Error;
import lexer.Token;
import parser.nodes.*;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;


public class SemanticAnalysis {
    private VariableTable variableTable = new VariableTable();
    /**scope用于标记变量所在的层次*/
    private int scope =0;
    /**需要进行语义分析的节点*/
    private Node programNode;
    /**用于标记while循环的层次，可能循环中有内嵌*/
    private int whileIndex = -1;
    private ArrayList<Boolean> isWhile = new ArrayList<>();
    /**多个函数的调用，存储各个函数是否被调用*/
    private ArrayList<Boolean> isFunction =new ArrayList<>();
    /**存储函数的返回值*/
    private ArrayList<Value> functionValue = new ArrayList<>();
    /**函数索引
     * 每调用一次就加1
     * 可用于获取函数返回值*/
    private int functionIndex = -1;
    /**首先先把函数节点存储，每次调用函数时就从中搜索对应函数节点*/
    private ArrayList<FunctionNode> functionNodes = new ArrayList<>();
    public String getResult() {
        return result;
    }

    /**用来存打印的值*/
    private String result;

    public SemanticAnalysis(Node programNode) {
        this.programNode = programNode;
    }

    public void run() throws CMMException {
        result=interpretProgram();
    }

    public String interpretProgram() throws CMMException {
        result = "";
        ArrayList<Node> children = programNode.getChildren();
        /**由parser可以知道这里的children的孩子只有一个stmtNode，可以直接取*/
        for(Node child:children){
            interpretStmt(child.getChildren(0),programNode);
        }
        return result;
    }
    /**
     * child:用于解析的节点，
     * formerNode:前一个解析的节点
     * */
    private void interpretStmt(Node child,Node formerNode) throws CMMException {
        if(child.getNodeType()==Node.DECLARE_STMT){
            interpretDeclStmt((DeclNode) child);
        }else if(child.getNodeType()==Node.IF_STMT){
            interpretIfStmt((IfNode)child);
        }else if(child.getNodeType()==Node.WHILE_STMT){
            interpretWhileStmt((WhileNode)child);
        }else if(child.getNodeType()==Node.BLOCK_STMT){
            interpretBlockStmt((BlockStmtNode) child,formerNode);
        }else if(child.getNodeType()==Node.READ_STMT){
            interpretReadStmt((ReadNode)child);
        }else if(child.getNodeType()==Node.WRITE_STMT){
            interpretWriteStmt((WriteNode) child);
        }else if(child.getNodeType()==Node.ASSIGN_STMT){
            interpretAssignStmt((AssignStmtNode) child);
        }else if(child.getNodeType()==Node.FUNCTION_STMT){
            if(formerNode.getNodeType()!=Node.PROGRAM){
                throw new CMMException(Error.FUNCTION_DECLARE_ERROR,Error.FUNCTION_DECLARE_ERROR+": The FUNCTION_STMT should be declared in the PROGRAM.",
                        child.getLineNumber(),child.getPosition());
            }
            functionNodes.add((FunctionNode)child);
        }else{
            throw new CMMException(Error.UNKNOWN_ERROR,"Unknown error.",child.getLineNumber(),child.getPosition());
        }
    }

    /** 解析声明语句 */
    private void interpretDeclStmt(DeclNode declNode) throws CMMException {
        //声明变量的类型
        int declareTokenType = declNode.getTokenType();
        Set<ValueNode> valueNodeSet = declNode.getHashMap().keySet();
        for(ValueNode valueNode:valueNodeSet){
            Variable variable = new Variable();
            variable.setScope(scope);
            variable = setVariableType(variable,declareTokenType);
            variable.setValueName(valueNode.getValue());
            //有过声明同名变量则报错
            if(variableTable.contains(variable, functionIndex)){
                Token errorToken = valueNode.getToken();
                throw new CMMException(Error.VARIABLE_EXISTS,Error.VARIABLE_EXISTS+": variable already exists at LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
            }
            //分析声明数组
            if(valueNode.isArray()){
                variable = generateArrayType(variable, valueNode.getType());
                if(valueNode.isHasValueNode()){
                    ValueNode valueNode1 = valueNode.getValueNode();
                    Variable variable1 = new Variable();
                    variable1.setValueName(valueNode1.getValue());
                    variable1.setScope(scope);
                    if(!variableTable.contains(variable1,functionIndex)){
                        Token errorToken = valueNode1.getToken();
                        throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable at LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                    }
                    variable1 = variableTable.getVariable(variable1,functionIndex);
                    if(variable1.getVariableType()!= Variable.VariableType.INT)//||
                            //variable1.getVariableType()!= Variable.VariableType.INT_ARRAY){
                    {
                        Token errorToken = valueNode1.getToken();
                        throw new CMMException(Error.NOT_INTEGER_TYPE,Error.NOT_INTEGER_TYPE+": The variable is not int type. at LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                    }
                    variable.setArrayLength(variable1.getIntValue());
                }else {
                    variable.setArrayLength(valueNode.getArrayLength());
                }
            }
            //分析变量，如果有赋值则解析
            ExprNode exprNode = declNode.getHashMap().get(valueNode);
            if(exprNode!=null){
                if(exprNode.isHasCompare()){
                    /*这里报错*/
//                    Token errorToken = ((OpNode)exprNode.getCompareNode()).getToken();
                    throw new CMMException(Error.EXPRESSION_ERROR,Error.EXPRESSION_ERROR+": The expression cannot have comparison. at LineNo: "+valueNode.getLineNumber(),valueNode.getLineNumber(),valueNode.getPosition());
                }
                else {
                    if(variable.isArray()){
                        /*报错 数组声明不能赋值*/
                        Token errorToken = valueNode.getToken();
                        throw new CMMException(Error.ARRAY_ASSIGN_ERROR,Error.ARRAY_ASSIGN_ERROR+": The array cannot be assigned. at LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                    }
                    Value value = interpretExpr(exprNode);
                    variable.setValue(value, valueNode);
                }
            }
            variableTable.addVariable(variable,functionIndex);
        }
    }

    /** 解析赋值语句 */
    private void interpretAssignStmt(AssignStmtNode assignStmtNode) throws CMMException {
        ValueNode valueNode = assignStmtNode.getValueNode();
        ExprNode exprNode = assignStmtNode.getExprNode();
        Value value = interpretExpr(exprNode);
        Variable variable = new Variable();
        variable.setValueName(valueNode.getValue());
        variable.setScope(scope);
        //找到该变量并赋值
        if(variableTable.contains(variable,functionIndex)){
            //解析数组赋值
            if(valueNode.isArray()&&variableTable.getVariable(variable,functionIndex).isArray()){
                /** 处理数组index是ValueNode的情况 */
                if(valueNode.isHasValueNode()) {
                    ValueNode valueNode1 = valueNode.getValueNode();
                    Variable variable1 = new Variable();
                    variable1.setValueName(valueNode1.getValue());
                    variable1.setScope(scope);
                    //index找不到则报错
                    if (!variableTable.contains(variable1,functionIndex)) {
                        Token errorToken = valueNode1.getToken();
                        throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable. LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());

                    }
                    variable1 = variableTable.getVariable(variable1,functionIndex);
                    //index的数据类型不为int则报错
                    if (variable1.getVariableType() != Variable.VariableType.INT)//||
                    //variable1.getVariableType()!= Variable.VariableType.INT_ARRAY){
                    {
                        Token errorToken = valueNode1.getToken();
                        throw new CMMException(Error.NOT_INTEGER_TYPE,Error.NOT_INTEGER_TYPE+": The variable is not INT type. LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                    }
                    int index = variable1.getIntValue();
                    //赋值
                    variableTable.getVariable(variable,functionIndex).setArrayIndexOf(index, value, valueNode);
                } else{
                    /** 处理数组index为数字的情况 */
                    int index = valueNode.getArrayLength();
                    variableTable.getVariable(variable,functionIndex).setArrayIndexOf(index, value, valueNode);
                }
            }else if(!valueNode.isArray()&&!variableTable.getVariable(variable,functionIndex).isArray()){
                //变量赋值(不能与数组名字冲突)
                variableTable.getVariable(variable,functionIndex).setValue(value, valueNode);
            }else {
                Token errorToken = valueNode.getToken();
                System.err.println("数组赋值错误。");
                throw new CMMException(Error.ARRAY_ASSIGN_ERROR,Error.ARRAY_ASSIGN_ERROR+": The array assignment error.LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());

            }
        }else {
            //找不到该变量则报错
            Token errorToken = valueNode.getToken();
            throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable. LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
        }
    }

    /** 解析If语句 */
    private void interpretIfStmt(IfNode ifNode) throws CMMException {
        ExprNode exprNode = ifNode.getExprNode();
        Value value = interpretExpr(exprNode);
        if(value.isString()){
            throw new CMMException(Error.EXPRESSION_ERROR,Error.EXPRESSION_ERROR+": The current expression cannot be string type.LineNo: "+ifNode.getLineNumber(),ifNode.getLineNumber(),exprNode.getPosition());
        }
        //IF条件判断为真
        if(value.getOnlyValue()!=0){
            scope++;
            interpretBlockStmt((BlockStmtNode) ifNode.getBlockStmtNode1(), ifNode);
        }else {
            //IF条件判断为假
            scope++;
            if(ifNode.getBlockStmtNode2()!=null)
                interpretBlockStmt((BlockStmtNode) ifNode.getBlockStmtNode2(),ifNode);
        }
        /*结束后If语句之后要删除当中声明的节点*/
        if(functionIndex<0){
            for(int i=0;i<variableTable.size();i++){
                if(variableTable.getList().get(i).getScope()==scope){
                    variableTable.remove(variableTable.getList().get(i),functionIndex);
                }
            }
        }else {
            for(int i=0;i<variableTable.getFunctionTable(functionIndex).size();i++){
                VariableTable functionTable = variableTable.getFunctionTable(functionIndex);
                if(functionTable.getList().get(i).getScope()==scope){
                    functionTable.remove(functionTable.getList().get(i),-1);
                }
            }
        }
        scope--;
    }

    /** 解析while语句 */
    private void interpretWhileStmt(WhileNode whileNode) throws CMMException {
        ExprNode exprNode = whileNode.getExprNode();
        Value value = interpretExpr(exprNode);
        scope++;
        whileIndex++;
        isWhile.add(true);
        //while条件判断为真
        while (value.getOnlyValue()!=0){
            interpretBlockStmt((BlockStmtNode) whileNode.getBlockStmtNode(),whileNode);
            value = interpretExpr(exprNode);
            /*每次循环结束之后要删除当中声明的节点*/
            if(functionIndex<0){
                for(int i=0;i<variableTable.size();i++){
                    if(variableTable.getList().get(i).getScope()==scope){
                        variableTable.remove(variableTable.getList().get(i),functionIndex);
                    }
                }
            }else {
                for(int i=0;i<variableTable.getFunctionTable(functionIndex).size();i++){
                    VariableTable functionTable = variableTable.getFunctionTable(functionIndex);
                    if(functionTable.getList().get(i).getScope()==scope){
                        functionTable.remove(functionTable.getList().get(i),-1);
                    }
                }
            }
            //检测是否有break语句
            if(!isWhile.get(whileIndex)){
                isWhile.remove(whileIndex);
                break;
            }
        }
        whileIndex--;
        scope--;
    }

    /** 解析BLOCK语句 */
    private void interpretBlockStmt(BlockStmtNode blockStmt, Node formerNode) throws CMMException {
        ArrayList<Node> children = blockStmt.getStmtNode();
        for(Node child:children){
            //是否在函数中
            if(functionIndex>=0&&!isFunction.get(functionIndex))
                return;
            for(Node c:child.getChildren()){
                if(c.getNodeType()==Node.RETURN_STMT){
                    boolean shouldReturn = false;
                    if(isFunction.get(functionIndex)) shouldReturn = true;
                    Value value = interpretReturnNode((ReturnNode) c,shouldReturn);
                    functionValue.set(functionIndex,value);
                    isFunction.set(functionIndex,false);
                    return;
                }
                if(c.getNodeType()!=Node.BREAK_STMT){
                    interpretStmt(c, formerNode);
                }else {
                    if(isWhile.get(whileIndex)){
                        isWhile.set(whileIndex,false);
                        return;
                    }else {
                        throw new CMMException(Error.UNEXPECTED_STATMENT,Error.UNEXPECTED_STATMENT+": Unexpected BREAK statement. lineNo: "+c.getLineNumber(),c.getLineNumber(),c.getPosition());
                    }
                }
            }
        }
    }

    /** 解析write语句 */
    private void interpretWriteStmt(WriteNode writeNode) throws CMMException {
        if(writeNode.getStringConst()!=null){
            String s = writeNode.getStringConst().getValue();
            //输出String时去掉引号
            result = result+s.substring(1,s.length()-1)+"\n";
        }else {
            Value value = interpretExpr(writeNode.getExprNode());
            result = result+value.toString()+"\n";
        }
        System.out.println(result);
    }

    /** 解析read语句 */
    private void interpretReadStmt(ReadNode readNode) throws CMMException {
        ValueNode valueNode = readNode.getValueNode();
        Variable variable = new Variable();
        variable.setValueName(valueNode.getValue());
        variable.setScope(scope);
        String input="";
//        Scanner scanner=new Scanner(System.in);
        if(!variableTable.contains(variable,functionIndex)){
            Token errorToken = valueNode.getToken();
            throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable. LineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
        }else {
//            input = scanner.next();
            input = JOptionPane.showInputDialog(variable.getValueName()+": ");
            variable = variableTable.getVariable(variable,functionIndex);
            if(variable.isArray()){
                //存值在数组里
                if(variable.getVariableType()== Variable.VariableType.INT_ARRAY){
                    variable.getIntArray()[valueNode.getArrayLength()] = Integer.parseInt(input);
                }else if(variable.getVariableType()== Variable.VariableType.DOUBLE_ARRAY){
                    variable.getDoubleArray()[valueNode.getArrayLength()] = Double.parseDouble(input);
                }else if(variable.getVariableType()== Variable.VariableType.BOOL_ARRAY){
                    variable.getBoolArray()[valueNode.getArrayLength()] = Boolean.parseBoolean(input);
                }
            }else {
                //存值在变量里
                if(variable.getVariableType()== Variable.VariableType.INT){
                    variable.setIntValue(Integer.parseInt(input));
                }else if(variable.getVariableType()== Variable.VariableType.DOUBLE){
                    variable.setDoubleValue( Double.parseDouble(input));
                }else if(variable.getVariableType()== Variable.VariableType.BOOL){
                    variable.setBoolValue( Boolean.parseBoolean(input));
                }else if(variable.getVariableType()== Variable.VariableType.STRING){
                    variable.setStringValue(input);
                }
            }
        }

    }

    private Variable generateArrayType(Variable variable, int type) {
        if(type==Token.INT)
            variable.setVariableType(Variable.VariableType.INT_ARRAY);
        else if(type==Token.DOUBLE)
            variable.setVariableType(Variable.VariableType.DOUBLE_ARRAY);
        else if(type==Token.BOOL)
            variable.setVariableType(Variable.VariableType.BOOL_ARRAY);
        return variable;
    }

    private  Variable setVariableType(Variable variable, int declareTokenType){
        if(declareTokenType == Token.BOOL)
            variable.setVariableType(Variable.VariableType.BOOL);
        else if(declareTokenType == Token.STRING)
            variable.setVariableType(Variable.VariableType.STRING);
        else if(declareTokenType == Token.INT)
            variable.setVariableType(Variable.VariableType.INT);
        else if(declareTokenType == Token.DOUBLE)
            variable.setVariableType(Variable.VariableType.DOUBLE);
        return variable;
    }

    /** 解析Expr语句 */
    private Value interpretExpr(ExprNode exprNode) throws CMMException {
        /*如果有比较的话*/
        Value value;
        if(exprNode.isHasCompare()){
            value = interpretOpNode((OpNode)exprNode.getTermNodeOrOpNode());
        }else{
            /* 没有比较的话为TermNode或opNode */
            if(exprNode.getTermNodeOrOpNode().getNodeType()==Node.TERM){
                value = interpretTermNode((TermNode) exprNode.getTermNodeOrOpNode());
            }else {
                /* 说明是个opNode */
                value = interpretOpNode((OpNode) exprNode.getTermNodeOrOpNode());
            }
        }
        return value;
    }

    /** 解析Term语句
     *  Factor或OpNode
     * */
    private Value interpretTermNode(TermNode termNode) throws CMMException {
        Value value;
        if(termNode.getFactorOrOpNode().getNodeType()==Node.FACTOR){
            value = interpretFactorNode((FactorNode) termNode.getFactorOrOpNode());
        }else {
            value = interpretOpNode((OpNode) termNode.getFactorOrOpNode());
        }
        return value;
    }

    /** 解析FactorNode */
    private Value interpretFactorNode(FactorNode factorNode) throws CMMException {
        Value value = new Value();
        if(factorNode.getExprNode()!=null){
            //嵌套Expr情况
            value = interpretExpr(factorNode.getExprNode());
        }else if(factorNode.getValueNode()!=null){
            /*变量名*/
            ValueNode valueNode=factorNode.getValueNode();
            String variableName = factorNode.getValueNode().getValue();
            Variable variable = new Variable();
            variable.setScope(scope);
            variable.setValueName(variableName);
            //数组的情况
            if(valueNode.isHasValueNode()){
                ValueNode valueNodeIndex = valueNode.getValueNode();
                Variable variable1 = new Variable();
                variable1.setScope(scope);
                variable1.setValueName(valueNodeIndex.getValue());
                if(!variableTable.contains(variable,functionIndex)){
                    /*找不到变量要报错*/
                    Token errorToken = valueNode.getToken();
                    throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable. lineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                }
                variable = variableTable.getVariable(variable,functionIndex);
                if(variableTable.contains(variable1,functionIndex)){
                    variable1 = variableTable.getVariable(variable1,functionIndex);
                    if(variable1.getVariableType()== Variable.VariableType.INT){
                        int index = variable1.getIntValue();
                        value.setValue(variable,index);
                    }else {
                        /*index非整型要报错*/
                        Token errorToken = valueNodeIndex.getToken();
                        throw new CMMException(Error.NOT_INTEGER_TYPE,Error.NOT_INTEGER_TYPE+": The variable is not INT type. lineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                    }
                }
                //调用函数的情况
            }else if(valueNode.isCallFunctionNode()){
                FunctionNode functionNode = getCalledFunction(valueNode);
                if(functionNode==null){
                    Token errorToken = valueNode.getToken();
                    throw new CMMException(Error.FUNCTION_NOT_FOUND, Error.FUNCTION_NOT_FOUND+": Cannot find responding function. lineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                }
                ArrayList<Value> values = new ArrayList<>();
                for(int i=0;i<valueNode.getExprNodes().size();i++){
                    ExprNode exprNode = valueNode.getExprNodes().get(i);
                    Value parameter = interpretExpr(exprNode);
                    values.add(parameter);
                }
                value = interpretFunctionNode(functionNode,values);
            }else {
                //变量的情况
                if(variableTable.contains(variable,functionIndex)){
                    variable = variableTable.getVariable(variable,functionIndex);
                    value.setValue(variable, factorNode.getValueNode().getArrayLength());
                }else {
                /*在变量表中找不到
                * 需要报错*/
                    Token errorToken = valueNode.getToken();
                    throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable. lineNo: "+errorToken.getLineNo(),errorToken.getLineNo(),errorToken.getPosition());
                }
            }
//
//            if(!valueNode.isHasValueNode()){
//                if(variableTable.contains(variable)){
//                    variable = variableTable.getVariable(variable);
//                    value.setValue(variable, factorNode.getExprNode().getArrayLength());
//                }else {
//                /*在变量表中找不到
//                * 需要报错*/
//                    Token errorToken = valueNode.getToken();
//                    throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable",errorToken.getLineNo(),errorToken.getPosition());
//                }
//            }
//            else {
//                ValueNode valueNodeIndex = valueNode.getExprNode();
//                Variable variable1 = new Variable();
//                variable1.setScope(scope);
//                variable1.setValueName(valueNodeIndex.getValue());
//                if(!variableTable.contains(variable)){
//                    /*不存在变量要报错*/
//                    Token errorToken = valueNode.getToken();
//                    throw new CMMException(Error.VARIABLE_NOT_FOUND,Error.VARIABLE_NOT_FOUND+": Cannot found declared variable",errorToken.getLineNo(),errorToken.getPosition());
//                }
//                variable = variableTable.getVariable(variable);
//                if(variableTable.contains(variable1)){
//                    variable1 = variableTable.getVariable(variable1);
//                    if(variable1.getVariableType()== Variable.VariableType.INT){
//                        int index = variable1.getIntValue();
//                        value.setValue(variable,index);
//                    }else {
//                        /*非整型要报错*/
//                        Token errorToken = valueNodeIndex.getToken();
//                        throw new CMMException(Error.NOT_INTEGER_TYPE,Error.NOT_INTEGER_TYPE+": The variable is not INT type.",errorToken.getLineNo(),errorToken.getPosition());
//                    }
//                }
//            }
        }else {
            //单独的值
            value.setValue(factorNode);
        }
        return value;
    }

    private Value interpretFunctionNode(FunctionNode functionNode, ArrayList<Value> values) throws CMMException {
        if(functionNode.getType()==Token.VOID){
            throw new CMMException(Error.FUNCTION_DECLARE_ERROR,Error.FUNCTION_DECLARE_ERROR+": The calling functiong cannot be void type. ",
                    functionNode.getLineNumber(),functionNode.getPosition());
        }

        if(functionNode.getValueNodes().size()!=values.size()){
            throw new CMMException(Error.VARIABLE_ASSIGN_ERROR,Error.VARIABLE_ASSIGN_ERROR+": Wrong assigned parameter(s) to the calling function.",
                    functionNode.getLineNumber(),functionNode.getPosition());
        }
        scope++;
        VariableTable funcVariableTable = new VariableTable();
        functionIndex++;
        variableTable.addFunctionTable(funcVariableTable);
        variableTable.setInFunction(true);
        isFunction.add(true);
        functionValue.add(null);
        ArrayList<Variable> functionParams = new ArrayList<>();
        for(int i=0;i<functionNode.getValueNodes().size();i++){
            ValueNode valueNode = functionNode.getValueNodes().get(i);
            Value value = values.get(i);
            if(!checkValueType(valueNode,value))
                throw new CMMException(Error.VARIABLE_ASSIGN_ERROR,Error.VARIABLE_ASSIGN_ERROR+": Wrong assigned parameter(s) to the calling function.",
                        valueNode.getLineNumber(),valueNode.getPosition());
            Variable variable = new Variable();
            variable.setValueName(valueNode.getValue());
            variable = setVariableType(variable,valueNode.getType());
            variable.setValue(value,valueNode);
            variable.setScope(scope);
            functionParams.add(variable);
        }
        if(functionParams.size()>0){
            for(Variable variable:functionParams){
                if(!variableTable.funcContains(variable,functionIndex)) {
                    variableTable.addVariable(variable,functionIndex);
                }
                else {
                    throw new CMMException(Error.REDUNDENT_DECLARATION,Error.REDUNDENT_DECLARATION+": Redundant variable declared for "+variable.getValueName(),
                            functionNode.getLineNumber(),functionNode.getPosition());
                }
            }
        }

        BlockStmtNode blockStmtNode = functionNode.getBlockStmtNode();
//        Value value = interpretFunctionBlockStmt(blockStmtNode, functionNode);
        interpretBlockStmt(blockStmtNode, functionNode);
        Value value = functionValue.get(functionIndex);
        if(value==null){
            throw new CMMException(Error.FUNCTION_DECLARE_ERROR,Error.FUNCTION_DECLARE_ERROR+": The calling function should have return value.",
                    functionNode.getLineNumber(),functionNode.getPosition());
        }
        this.variableTable.removeFunctionTable(functionIndex);
        isFunction.remove(functionIndex);
        functionIndex--;
        scope--;
        return value;
    }


    private Value interpretReturnNode(ReturnNode returnNode, boolean shouldReturn) throws CMMException {
        if(returnNode.getExprNode()!=null&& !shouldReturn){
            throw new CMMException(Error.FUNCTION_DECLARE_ERROR,Error.FUNCTION_DECLARE_ERROR+": The calling function should not have return value.",
                    returnNode.getLineNumber(),returnNode.getPosition());
        }else if(returnNode.getExprNode()==null&&shouldReturn){
            throw new CMMException(Error.FUNCTION_DECLARE_ERROR,Error.FUNCTION_DECLARE_ERROR+": The calling function should have return value.",
                    returnNode.getLineNumber(),returnNode.getPosition());
        }else {
            if(returnNode.getExprNode()==null){
                return null;
            }
            return interpretExpr(returnNode.getExprNode());
        }
    }

    private FunctionNode getCalledFunction(ValueNode valueNode) {
        for (FunctionNode functionNode : functionNodes) {
            if (valueNode.getValue().equals(functionNode.getName())) {
                return functionNode;
            }
        }
        return null;
    }
    private boolean checkValueType(ValueNode valueNode, Value value) throws CMMException {
        if(valueNode.getType()==Token.INT&&!value.isInt()){
            return false;
        }else if(valueNode.getType()==Token.STRING&&!value.isString())
            return false;
        else if(valueNode.getType()==Token.BOOL&&!value.isBool()){
            return false;
        }else if(valueNode.getType()!=Token.STRING &&value.isString()){
            return false;
        }
        return true;
    }

    private Value interpretOpNode(OpNode opNode) throws CMMException {
        Value left;
        Value right;
        if(opNode.getLeft().getNodeType()==Node.TERM&&opNode.getRight().getNodeType()==Node.TERM){
            // /两边都是termNode
            left = interpretTermNode((TermNode)opNode.getLeft());
            right = interpretTermNode((TermNode)opNode.getRight());
        }else if(opNode.getLeft().getNodeType()==Node.FACTOR&&opNode.getRight().getNodeType()==Node.FACTOR){
            // /两边都是factorNode
            left = interpretFactorNode((FactorNode)opNode.getLeft());
            right = interpretFactorNode((FactorNode)opNode.getRight());
        }else if(opNode.getLeft().getNodeType()!=Node.TERM&&opNode.getLeft().getNodeType()!=Node.FACTOR&&opNode.getRight().getNodeType()==Node.FACTOR){
            //右边是factorNode
            left = interpretOpNode((OpNode)opNode.getLeft());
            right = interpretFactorNode((FactorNode)opNode.getRight());
        }else if(opNode.getRight().getNodeType()!=Node.TERM&&opNode.getRight().getNodeType()!=Node.FACTOR&&opNode.getLeft().getNodeType()==Node.FACTOR){
            //左边是factorNode
            left = interpretFactorNode((FactorNode)opNode.getLeft());
            right = interpretOpNode((OpNode)opNode.getRight());
        }else if(opNode.getLeft().getNodeType()!=Node.TERM&&opNode.getLeft().getNodeType()!=Node.FACTOR&&opNode.getRight().getNodeType()==Node.TERM){
            //右边是termNode
            left = interpretOpNode((OpNode)opNode.getLeft());
            right = interpretTermNode((TermNode)opNode.getRight());
        }else if(opNode.getRight().getNodeType()!=Node.TERM&&opNode.getRight().getNodeType()!=Node.FACTOR&&opNode.getLeft().getNodeType()==Node.TERM){
            //左边是termNode
            left = interpretTermNode((TermNode)opNode.getLeft());
            right = interpretOpNode((OpNode)opNode.getRight());
        }else{
            //两边都是OpNode
            left = interpretOpNode((OpNode)opNode.getLeft());
            right = interpretOpNode((OpNode)opNode.getRight());
        }
        return Value.calculate(left, right, opNode.getToken().getType());
    }
}
