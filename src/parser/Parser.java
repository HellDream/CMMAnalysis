package parser;

import exceptions.CMMException;
import exceptions.Error;
import lexer.Lexer;
import lexer.Token;
import parser.nodes.*;
import sun.java2d.cmm.kcms.CMM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 79300 on 2017/11/6.
 */
public class Parser {
    private Lexer lexer;
    private ArrayList<Token> tokens= new ArrayList<>();
    private Token currentToken;
    private Token nextToken;
    private Iterator<Token> iterator;
    private Node program = null;


    public Node getProgram() {
        return program;
    }
    StringBuilder sb = new StringBuilder();

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public Parser(String text) throws CMMException {
        lexer = new Lexer(text);
        lexer.runLexer();

        //词法分析不通过时不进行语法分析
        if(!lexer.getErrors().isEmpty()){
            throw new CMMException(Error.TOKEN_ERROR,Error.TOKEN_ERROR+": Lexical Analysis Failed",0,0);
        }else {
            tokens = lexer.getTokens();
            currentToken = null;
            nextToken = tokens.get(0);
            iterator = tokens.iterator();
            iterator.next();
        }
    }

    public void run() throws IOException, CMMException {
        program = parseProgram();
//        traverse(program);
    }

    public String getParserResult(){
        return traverse(program);
    }

    private String traverse(Node node){
        sb.append(node.toString()+"\n");
        for(Node mNode:node.getChildren()){
            traverse(mNode);
        }
        return sb.toString();
    }

    private void move(){
        if(iterator.hasNext()){
            currentToken=nextToken;
            nextToken=iterator.next();
        }else {
            currentToken=nextToken;
            nextToken=null;
        }

    }

    private Node parseProgram() throws IOException, CMMException {
        Node programNode = new Node(Node.PROGRAM);
        while (nextToken!=null)
            programNode.add(parseStmt());
        return programNode;
    }

    private Node parseStmt() throws IOException, CMMException{
        Node stmtNode = new StmtNode();
        while (nextToken!=null&&(nextToken.getType()==Token.LEFTMULCOM||
        nextToken.getType()==Token.RIGHTMULCOM||nextToken.getType()==Token.SINGLECOM)){
            move();
        }
        if(nextToken==null){
            throw new CMMException(Error.TOKEN_ERROR,"Expect Statment after Token:" + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        stmtNode.setLineNumber(nextToken.getLineNo());
        stmtNode.setPosition(nextToken.getPosition());
        int tokenType = nextToken.getType();
        switch (tokenType){
            case Token.IF:
                stmtNode.add(parseIfStmt());
                return stmtNode;
            case Token.WHILE:
                stmtNode.add(parseWhileStmt());
                return stmtNode;
            case Token.READ:
                stmtNode.add(parseReadStmt());
                return stmtNode;
            case Token.WRITE:
                stmtNode.add(parseWriteStmt());
                return stmtNode;
            case Token.LBRACE:
                stmtNode.add(parseBlockStmt());
                return stmtNode;
            case Token.ID:
                stmtNode.add(parseAssignStmt());
                return stmtNode;
            case Token.BREAK:
                stmtNode.add(parseBreakStmt());
                return stmtNode;
            case Token.INT:
            case Token.DOUBLE:
            case Token.STRING:
            case Token.BOOL:
            case Token.VOID:
                stmtNode.add(parseDeclareStmt());
                return stmtNode;
            case Token.DEF:
                stmtNode.add(parseFunction());
                return stmtNode;
            case Token.RETURN:
                stmtNode.add(parseReturnStmt());
                return stmtNode;
            default:
                throw new CMMException(Error.TOKEN_ERROR,"Unexpected token: " + nextToken.toString(),
                        nextToken.getLineNo(),nextToken.getPosition());
        }
    }

    private ReturnNode parseReturnStmt() throws IOException, CMMException {
        move();
        ReturnNode returnNode = new ReturnNode();
        returnNode.add(new Node(currentToken));
        returnNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if(nextToken!=null&&(nextToken.getType()==Token.ID||isNumConstant(nextToken.getType())||nextToken.getType()==Token.STRING_TEXT)){
            ExprNode exprNode = parseExpr();
            returnNode.add(exprNode);
            returnNode.setValueNode(exprNode);
        }
        if(nextToken!=null&&nextToken.getType()==Token.SEMI){
            move();
            returnNode.add(new Node(currentToken));
        }else {
            throw new CMMException(Error.TOKEN_ERROR,"Expect token ';' after token " + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        return returnNode;
    }

    private IfNode parseIfStmt() throws IOException, CMMException {
        /**IF (EXPR) STMT  (ELSE STMT) */
        IfNode ifNode = new IfNode();
        move();
        ifNode.add(new Node(currentToken));
        ifNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if(nextToken.getType()!=Token.LP){
            throw new CMMException(Error.TOKEN_ERROR,"Expect '(' but get token:"+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        ifNode.add(new Node(currentToken));
        ExprNode expr = parseExpr();
        ifNode.add(expr);
        ifNode.setExprNode(expr);
        if(nextToken.getType()!=Token.RP){
            throw new CMMException(Error.TOKEN_ERROR,"Expect ')' but get token:"+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        ifNode.add(new Node(currentToken));
        if(nextToken==null){
            throw new CMMException(Error.TOKEN_ERROR,"Expect '{' or statments! after TOKEN:" + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        BlockStmtNode stmtNode = parseBlockStmt();
        ifNode.add(stmtNode);
        ifNode.setBlockStmtNode1(stmtNode);
        if(nextToken==null||nextToken.getType()!=Token.ELSE)
            return ifNode;
        move();
        ifNode.add(new Node(currentToken));
        BlockStmtNode stmtNode2 = parseBlockStmt();
        ifNode.add(stmtNode2);
        ifNode.setBlockStmtNode2(stmtNode2);
        return ifNode;
    }

    private WhileNode parseWhileStmt() throws IOException, CMMException {
        /** WHILE (EXPR) STMT */
        WhileNode whileNode = new WhileNode();
        move();
        whileNode.add(new Node(currentToken));
        whileNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if(nextToken.getType()!=Token.LP){
            throw new CMMException(Error.TOKEN_ERROR,"Expect '(' but get token:"+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        whileNode.add(new Node(currentToken));
        ExprNode exprNode = parseExpr();
        whileNode.add(exprNode);
        whileNode.setExprNode(exprNode);
        if(nextToken==null){
            throw new CMMException(Error.TOKEN_ERROR,"Expect ')' token",
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        if(nextToken.getType()!=Token.RP){
            throw new CMMException(Error.TOKEN_ERROR,"Expect ')' but get token:"+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        whileNode.add(new Node(currentToken));
        BlockStmtNode stmtNode = parseBlockStmt();
        whileNode.add(stmtNode);
        whileNode.setBlockStmtNode(stmtNode);
        return whileNode;
    }

    private ReadNode parseReadStmt() throws IOException, CMMException {
        /** READ (EXPR|STRINGCONSTANT); */
        ReadNode readStmtNode = new ReadNode();
        move();
        readStmtNode.add(new Node(currentToken));
        readStmtNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if (nextToken.getType() != Token.LP) {
            throw new CMMException(Error.TOKEN_ERROR,"Expect '(' but get token:"+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        readStmtNode.add(new Node(currentToken));
//        if (nextToken.getType() != Token.STRING_TEXT) {
//            Node exprNode = parseExpr();
//            readStmtNode.add(exprNode);
//            readStmtNode.setExprNode(exprNode);
//        } else {
//            //STRING_TEXT
//            move();
//            readStmtNode.add(new Node(currentToken));
//            readStmtNode.setStringConst(currentToken);
//        }
        if(nextToken.getType()!=Token.ID){
            throw new CMMException(Error.TOKEN_ERROR,"Expected variable but get token: "+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        ValueNode valueNode = parseValue();
        readStmtNode.setValueNode(valueNode);
        //添加);
        if (nextToken.getType() != Token.RP) {
            throw new CMMException(Error.TOKEN_ERROR,"Expect ')' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        readStmtNode.add(new Node(currentToken));
        if (nextToken.getType() != Token.SEMI) {
            throw new CMMException(Error.TOKEN_ERROR,"Expect ';' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        readStmtNode.add(new Node(currentToken));
        return readStmtNode;
    }

    private WriteNode parseWriteStmt() throws IOException, CMMException {
        /** WRITE (EXPR|STRINGCONSTANT); */
        WriteNode writeStmtNode = new WriteNode();
        move();
        writeStmtNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());

        writeStmtNode.add(new Node(currentToken));
        if (nextToken.getType() != Token.LP) {
            throw new CMMException(Error.TOKEN_ERROR,"Expect '(' but get token:"+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        writeStmtNode.add(new Node(currentToken));
        if (nextToken.getType() != Token.STRING_TEXT) {
            ExprNode exprNode = parseExpr();
            writeStmtNode.add(exprNode);
            writeStmtNode.setExprNode(exprNode);
        } else {
            //STRING_TEXT
            move();
            writeStmtNode.add(new Node(currentToken));
            writeStmtNode.setStringConst(currentToken);
        }
        //添加);
        if (nextToken.getType() != Token.RP) {
            throw new CMMException(Error.TOKEN_ERROR,"Expect ')' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        writeStmtNode.add(new Node(currentToken));
        if (nextToken.getType() != Token.SEMI) {
            throw new CMMException(Error.TOKEN_ERROR,"Expect ';' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());        }
        move();
        writeStmtNode.add(new Node(currentToken));
        return writeStmtNode;
    }

    private BlockStmtNode parseBlockStmt() throws IOException, CMMException {
        /** {(STMT)* } */
        BlockStmtNode blockStmtNode = new BlockStmtNode();
        if(nextToken==null){
            throw new CMMException(Error.TOKEN_ERROR,"Expect '{' after token:" + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }else if(nextToken.getType()!=Token.LBRACE){
            throw new CMMException(Error.TOKEN_ERROR,"Expect '{' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        blockStmtNode.add(new Node(currentToken));
        blockStmtNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        while (nextToken!=null&&nextToken.getType()!=Token.RBRACE){
            Node stmtNode = parseStmt();
            blockStmtNode.add(stmtNode);
            blockStmtNode.addStmtNode(stmtNode);
        }
        move();
        blockStmtNode.add(new Node(currentToken));
        return blockStmtNode;
    }

    private AssignStmtNode parseAssignStmt() throws IOException, CMMException{
        /**  VALUE=EXPR;  */
        AssignStmtNode assignStmtNode = new AssignStmtNode();
        ValueNode valueNode = parseValue();
        assignStmtNode.add(valueNode);
        assignStmtNode.setValueNode(valueNode);
        assignStmtNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if(nextToken.getType()!=Token.ASSIGN){
            throw new CMMException(Error.TOKEN_ERROR,"Expect '=' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());          }
        move();
        assignStmtNode.add(new Node(currentToken));
        ExprNode exprNode = parseExpr();
        assignStmtNode.add(exprNode);
        assignStmtNode.setExprNode(exprNode);
        if(nextToken==null||nextToken.getType()!=Token.SEMI){
            throw new CMMException(Error.TOKEN_ERROR,"Expect ';' after token:" + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        move();
        assignStmtNode.add(new Node(currentToken));
//        if(nextToken==null||nextToken.getType()!=Token.RBRACE){
//            throw new IOException("Expect '}' after token:" + currentToken.toString());
//        }
//        move();
//        assignStmtNode.add(new Node(currentToken));
        return assignStmtNode;
    }

    private Node parseBreakStmt() throws IOException, CMMException {
        /** BREAK; */
        Node breakStmtNode = new Node(Node.BREAK_STMT);
        move();
        breakStmtNode.add(new Node(currentToken));
        breakStmtNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if(nextToken==null){
            throw new CMMException(Error.TOKEN_ERROR,"Expect ';' after token:" + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        if(nextToken.getType()!=Token.SEMI) {
            throw new CMMException(Error.TOKEN_ERROR,"Expect ';' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        move();
        breakStmtNode.add(new Node(currentToken));
        return breakStmtNode;
    }

    private Node parseDeclareStmt() throws IOException, CMMException{
        DeclNode declareStmtNode = new DeclNode();
        if(isType(nextToken.getType())){
            move();
            declareStmtNode.add(new Node(currentToken));
            declareStmtNode.setTokenType(currentToken.getType());
            declareStmtNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
            if(nextToken.getType()!=Token.ID){
                throw new CMMException(Error.TOKEN_ERROR,"Expect ID but get token:" + nextToken.toString(),
                        nextToken.getLineNo(),nextToken.getPosition());
            }
            move();
            ValueNode valueNode = new ValueNode(currentToken, declareStmtNode.getTokenType());
            valueNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
            declareStmtNode.add(new Node(currentToken));
            /**  Declare: ([INTCONSTANT])(=EXPR)(,VALUE(=EXPR))*;  */
            if(nextToken!=null&&(nextToken.getType()==Token.LBRACKET||nextToken.getType()==Token.ASSIGN
                    ||nextToken.getType()==Token.SEMI||nextToken.getType()==Token.COMMA)){
                //一定是声明
                declareStmtNode = parseDeclare(declareStmtNode, valueNode);
                return declareStmtNode;
            }else{
                throw new CMMException(Error.TOKEN_ERROR,"Expect '(' or '[' or '=' or ',' or ';' after token:" + currentToken.toString(),
                        currentToken.getLineNo(),currentToken.getPosition());
            }
        }else {
            //不是declareStmt
            throw new CMMException(Error.TOKEN_ERROR,"Expect int or double or string or bool after token:" + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
    }

    private FunctionNode parseFunction() throws CMMException, IOException {
        move();
        FunctionNode functionNode = new FunctionNode();
        functionNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if(isType(nextToken.getType())){
            /**函数类型*/
            move();
            functionNode.add(new Node(currentToken));
            functionNode.setToken(currentToken);
            if(nextToken==null||nextToken.getType()!=Token.ID){
                throw new CMMException(Error.TOKEN_ERROR,Error.TOKEN_ERROR+": Expect an ID token after token"+currentToken.toString(),
                        currentToken.getLineNo(),currentToken.getPosition());
            }
            move();
            functionNode.add(new Node(currentToken));
            functionNode.setFunctionNameToken(currentToken);
            if(nextToken==null||nextToken.getType()!=Token.LP){
                throw new CMMException(Error.TOKEN_ERROR,Error.TOKEN_ERROR+": Expect an '(' token after token"+currentToken.toString(),
                        currentToken.getLineNo(),currentToken.getPosition());
            }
            move();
            functionNode.add(new Node(currentToken));
            while(isType(nextToken.getType())){
                move();
                //当前为形参类型
                Token typeToken = currentToken;
                functionNode.add(new Node(currentToken));
                if(nextToken==null||nextToken.getType()!=Token.ID){
                    throw new CMMException(Error.TOKEN_ERROR,Error.TOKEN_ERROR+": Expect an ID token after token"+currentToken.toString(),
                            currentToken.getLineNo(),currentToken.getPosition());
                }
                move();
                ValueNode valueNode = new ValueNode(currentToken, typeToken.getType());
                valueNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
                functionNode.addValueNode(valueNode);
                if(nextToken.getType()==Token.COMMA){
                    move();
                }else if(nextToken.getType()==Token.RP){
                    break;
                }else {
                    throw new CMMException(Error.TOKEN_ERROR,Error.TOKEN_ERROR+": Invalid token after token"+currentToken.toString(),
                            currentToken.getLineNo(),currentToken.getPosition());
                }
            }
            move();
            functionNode.add(new Node(currentToken));
            if(nextToken==null||nextToken.getType()!=Token.LBRACE){
                throw new CMMException(Error.TOKEN_ERROR,Error.TOKEN_ERROR+": Expect '{' token after token"+currentToken.toString(),
                        currentToken.getLineNo(),currentToken.getPosition());
            }
            BlockStmtNode blockStmtNode = parseBlockStmt();
            functionNode.add(blockStmtNode);
            functionNode.setBlockStmtNode(blockStmtNode);
        }else {
            throw new CMMException(Error.TOKEN_ERROR,"Expect void or int or double or string or bool after token: "+currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        return functionNode;
    }

    private DeclNode parseDeclare(DeclNode declareStmtNode, ValueNode valueNode) throws IOException, CMMException {
        /** ([INTCONSTANT])(=EXPR)(,VALUE(=EXPR))*; */
        if(nextToken.getType()==Token.ASSIGN){
            /** =EXPR(,VALUE(=EXPR))*; */
            declareStmtNode = checkWithEQUAL(declareStmtNode, valueNode);

        }else if(nextToken.getType()==Token.LBRACKET){
            /** [INTCONSTANT](=EXPR)(,VALUE(=EXPR))*; */
            move();
            declareStmtNode.add(new Node(currentToken));
            if(nextToken.getType()!=Token.INT_NUMBER&&nextToken.getType()!=Token.ID){

                throw new CMMException(Error.TOKEN_ERROR,"Expected INT NUMBER or ID but get token:" + nextToken.toString(),
                        nextToken.getLineNo(),nextToken.getPosition());
            }
            if(nextToken.getType()==Token.INT_NUMBER){
                move();
                declareStmtNode.add(new Node(currentToken));
                Token numToken = currentToken;
                valueNode.setArray(true);
                valueNode.setArrayLength(Integer.parseInt(numToken.getValue()));
            }
            if(nextToken.getType()==Token.ID){
                ValueNode valueNode1 = parseValue();
                valueNode.setArray(true);
                valueNode.setValueNode(valueNode1);
            }
            if(nextToken.getType()!=Token.RBRACKET){
                throw new CMMException(Error.TOKEN_ERROR,"Expected ']' but get token:" + nextToken.toString(),
                        nextToken.getLineNo(),nextToken.getPosition());
            }
            move();
            declareStmtNode.add(new Node(currentToken));
            if(nextToken!=null&&nextToken.getType()==Token.ASSIGN){
                declareStmtNode = checkWithEQUAL(declareStmtNode, valueNode);
            }else if(nextToken!=null&&nextToken.getType()==Token.COMMA){
                declareStmtNode = checkWithCOMMA(declareStmtNode);
            }else {
                if(nextToken==null||nextToken.getType()!=Token.SEMI){
                    throw new CMMException(Error.TOKEN_ERROR,"Expected ';' after token:" + currentToken.toString(),
                            currentToken.getLineNo(),currentToken.getPosition());
                }
                move();
                declareStmtNode.put(valueNode,null);
                declareStmtNode.add(new Node(currentToken));
            }
            return declareStmtNode;
        }else if(nextToken.getType()==Token.COMMA){
            /** (,VALUE(=EXPR))*; */
            declareStmtNode = checkWithCOMMA(declareStmtNode);
        }else{
            declareStmtNode.put(valueNode,null);
            /** 下一个token为; */
            move();
            declareStmtNode.add(new Node(currentToken));
        }
        return declareStmtNode;
    }

    private DeclNode checkWithEQUAL(DeclNode declareStmtNode, ValueNode valueNode) throws IOException, CMMException{
        move();
        declareStmtNode.add(new Node(currentToken));
        ExprNode exprNode = parseExpr();
        declareStmtNode.add(exprNode);
        declareStmtNode.put(valueNode, exprNode);
        while(nextToken!=null&&nextToken.getType()==Token.COMMA){
            move();
            declareStmtNode.add(new Node(currentToken));
            valueNode = parseValue(declareStmtNode.getTokenType());
            declareStmtNode.add(valueNode);
            if(nextToken.getType()==Token.ASSIGN){
                move();
                declareStmtNode.add(new Node(currentToken));
                exprNode = parseExpr();
                declareStmtNode.add(exprNode);
                declareStmtNode.put(valueNode, exprNode);
            }else{
                declareStmtNode.put(valueNode,null);
            }
        }
        if(nextToken==null||nextToken.getType()!=Token.SEMI){
            throw new CMMException(Error.TOKEN_ERROR,"Expected ';' after token:" + currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        //加上分号
        move();
        declareStmtNode.add(new Node(currentToken));
        return declareStmtNode;
    }
    private DeclNode checkWithCOMMA(DeclNode declareStmtNode) throws IOException, CMMException {
        while(nextToken.getType()==Token.COMMA){
            move();
            declareStmtNode.add(new Node(currentToken));
            ValueNode valueNode = parseValue(declareStmtNode.getTokenType());
            declareStmtNode.add(valueNode);
            if(nextToken.getType()==Token.ASSIGN){
                move();
                declareStmtNode.add(new Node(currentToken));
                ExprNode exprNode = parseExpr();
                declareStmtNode.add(exprNode);
                declareStmtNode.put(valueNode,exprNode);
            }else{
                declareStmtNode.put(valueNode, null);
            }
        }
        if(nextToken.getType()!=Token.SEMI){
            throw new CMMException(Error.TOKEN_ERROR,"Expected ';' but get token:" + nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        //加上分号
        move();
        declareStmtNode.add(new Node(currentToken));
        return declareStmtNode;
    }

    private ValueNode parseValue(int valueType) throws IOException, CMMException {
        /** IDENT [INT_NUMBER] | IDENT */
        ValueNode valueNode = new ValueNode(valueType);
        //IDENT
        move();
        valueNode.add(new Node(currentToken));
        valueNode.setToken(currentToken);
        valueNode.setValue(currentToken.getValue());
        valueNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        if (nextToken!=null&&nextToken.getType() == Token.LBRACKET) {
            //[INT_NUMBER]
            move();
            valueNode.add(new Node(currentToken));
            valueNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
            if (nextToken.getType() != Token.INT_NUMBER&&nextToken.getType() != Token.ID) {
                throw new CMMException(Error.TOKEN_ERROR,"Expect IntNumber or ID but get token:" + nextToken.toString(),
                        nextToken.getLineNo(),nextToken.getPosition());
            }
            move();
            valueNode.add(new Node(currentToken));
            valueNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
            if(currentToken.getType()==Token.INT_NUMBER){
                valueNode.setArrayLength(Integer.parseInt(currentToken.getValue()));
            }else {
                valueNode.setArray(true);
                valueNode.setValueNode(new ValueNode(currentToken, Token.INT));
            }
            if (nextToken.getType() != Token.RBRACKET) {
                throw new CMMException(Error.TOKEN_ERROR,"Expect ']' but get token:" + nextToken.toString(),
                        nextToken.getLineNo(),nextToken.getPosition());
            }
            move();
            valueNode.add(new Node(currentToken));
            valueNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        }else if(nextToken!=null&&nextToken.getType()==Token.LP){
            //如果是调用函数的ValueNode
            move();
            valueNode.add(new Node(currentToken));
            valueNode.setCallFunctionNode(true);
            while (nextToken!=null&&nextToken.getType()!=Token.RP){
                ExprNode exprNode = parseExpr();
                valueNode.add(exprNode);
                valueNode.getExprNodes().add(exprNode);
                if(nextToken!=null&&nextToken.getType()==Token.COMMA){
                    move();
                    valueNode.add(new Node(currentToken));
                }
            }
            if(nextToken==null){
                throw new CMMException(Error.TOKEN_ERROR,Error.TOKEN_ERROR+": Expect ')' token after token"+currentToken.toString(),
                        currentToken.getLineNo(),currentToken.getPosition());
            }
            move();
            valueNode.add(new Node(currentToken));
        }
        return valueNode;
    }
    /**parseValue 传递默认值，用来表示该Value的类型未定 */
    private ValueNode parseValue() throws IOException, CMMException{
        return parseValue(Token.NULL);
    }

    private ExprNode parseExpr() throws IOException, CMMException {
        /** TERM((+|-)TERM)*(>|<|==|!=|>=|<=TERM((+|-)TERM)*)  */
        ExprNode exprNode = new ExprNode();
        exprNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        Node opOrTerm = parseTerm();
        exprNode.add(opOrTerm);

        while(nextToken!=null&&(nextToken.getType()==Token.ADD||nextToken.getType()==Token.SUBTRACT
                ||nextToken.getValue().matches("(-)[1-9][0-9]*(/.)?[0-9]*"))){
            Token opToken;
            if(nextToken.getValue().matches("(-)[1-9][0-9]*(/.)?[0-9]*")){
                opToken = new Token(Token.ADD,"+",currentToken.getLineNo(),currentToken.getPosition());
                exprNode.add(new Node(opToken));
            }else {
                move();
                opToken=currentToken;
                exprNode.add(new Node(currentToken));
            }
            Node rightTerm = parseTerm();
            opOrTerm = new OpNode(opToken, opOrTerm,rightTerm);
            exprNode.add(rightTerm);
        }
        exprNode.setTermNodeOrOpNode(opOrTerm);
        //有比较的情况
        if(nextToken!=null&&isComparison(nextToken.getType())){
            move();
            exprNode.add(new Node(currentToken));
            //存下比较运算符的token
            Token compareToken = currentToken;
            Node opOrTerm2 = parseTerm();
            exprNode.add(opOrTerm2);
            //这里没写好呢
            while(nextToken!=null&&(nextToken.getType()==Token.ADD||nextToken.getType()==Token.SUBTRACT
                    ||nextToken.getValue().matches("(-)[1-9][0-9]*(/.)?[0-9]*"))){
                Token opToken;
                if(nextToken.getValue().matches("(-)[1-9][0-9]*(/.)?[0-9]*")) {
                    opToken = new Token(Token.ADD, "+", currentToken.getLineNo(), currentToken.getPosition());
                    exprNode.add(new Node(opToken));
                }else {
                    move();
                    opToken = currentToken;
                    exprNode.add(new Node(currentToken));
                }
                Node rightCompare = parseTerm();
                exprNode.add(rightCompare);
                opOrTerm2 = new OpNode(opToken,opOrTerm2,rightCompare);
            }
            opOrTerm = new OpNode(compareToken, opOrTerm, opOrTerm2);
            exprNode.setTermNodeOrOpNode(opOrTerm);
            exprNode.setCompareNode(opOrTerm2);
            exprNode.setHasCompare(true);
            return exprNode;
        }
        return exprNode;
    }

    private TermNode parseTerm() throws IOException, CMMException {
        /** FACTOR((*|/)FACTOR)* */
        TermNode termNode = new TermNode();
        Node factorOrOpNode = parseFactor();
        termNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        termNode.add(factorOrOpNode);
        while(nextToken!=null&&(nextToken.getType()==Token.MUL||nextToken.getType()==Token.DIV)){
            move();
            Token opToken = currentToken;
            termNode.add(new Node(currentToken));
            Node rightFactor = parseFactor();
            factorOrOpNode = new OpNode(opToken, factorOrOpNode, rightFactor);
            termNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        }
        termNode.setFactorOrOpNode(factorOrOpNode);
        return termNode;
    }

    private FactorNode parseFactor() throws IOException, CMMException {
        /** VALUE|INTCONSTANT|DOUBLECONSTANT|TRUE|FALSE|(EXPR) */
        FactorNode factorNode = new FactorNode();
        if(nextToken==null){
            throw new CMMException(Error.TOKEN_ERROR,"Expect VALUE or CONSTANT after token:"+currentToken.toString(),
                    currentToken.getLineNo(),currentToken.getPosition());
        }
        if(nextToken.getType()==Token.ID){
            ValueNode valueNode = parseValue();
            factorNode.add(valueNode);
            factorNode.setNode(valueNode);
        }else if(isNumConstant(nextToken.getType())){
            move();
            Node endTokenNode = new Node(currentToken);
            factorNode.add(endTokenNode);
            factorNode.setValue(endTokenNode);
        }else if(nextToken.getType()==Token.STRING_TEXT){
            move();
            Node endTokenNode = new Node(currentToken);
            factorNode.add(endTokenNode);
            factorNode.setValue(endTokenNode);
        }
        else if(nextToken.getType()==Token.LP){
            move();
            factorNode.add(new Node(currentToken));
            ExprNode exprNode  =parseExpr();
            factorNode.add(exprNode);
            factorNode.setExprNode(exprNode);
            if(nextToken.getType()!=Token.RP)
                throw new CMMException(Error.TOKEN_ERROR,"Expect ')' but get token:"+nextToken.toString(),nextToken.getLineNo(),nextToken.getPosition());
            move();
            factorNode.add(new Node(currentToken));
        }else {
            throw new CMMException(Error.TOKEN_ERROR,"Expect ID or ID[] or NUMCONSTANT or '(' but get token:"+nextToken.toString(),
                    nextToken.getLineNo(),nextToken.getPosition());
        }
        factorNode.setLineNumberAndPosition(currentToken.getLineNo(),currentToken.getPosition());
        return factorNode;
    }

    private boolean isComparison(int type){
        return type==Token.MORE||type==Token.LESS||type==Token.EQUAL||type==Token.NOTEQ||type==Token.LESSEQ||type==Token.MOREEQ;
    }
    private boolean isNumConstant(int type){
        return type==Token.INT_NUMBER||type==Token.DOUBLE_NUMBER||type==Token.TRUE||type==Token.FALSE;
    }
    private boolean isType(int type){
        return type==Token.INT||type==Token.DOUBLE||type==Token.STRING||type==Token.BOOL;
    }

}
