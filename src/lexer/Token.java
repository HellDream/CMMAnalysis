package lexer;

import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * Created by 79300 on 2017/10/8.
 */
public class Token {

    //定义token类型
    /**保留字*/
    public static final int IF = 0;
    public static final int ELSE = 1;
    public static final int WHILE = 2;
    public static final int READ = 3;
    public static final int WRITE = 4;
    public static final int INT = 5;
    public static final int DOUBLE = 6;
    public static final int BOOL = 7;
    public static final int TRUE = 8;
    public static final int FALSE = 9;
    public static final int STRING = 10;
    public static final int CHAR = 11;
    public static final int NULL =12;
    public static final int VOID = 13;
    public static final int BREAK = 41;

    /** + */
    public static final int ADD =14;
    /** - */
    public static final int SUBTRACT = 15;
    /** * */
    public static final int MUL = 16;
    /** / */
    public static final int DIV= 17;
    /** = */
    public static final int ASSIGN = 18;
    /** < */
    public static final int LESS = 19;
    /** <= */
    public static final int LESSEQ = 20;
    /** > */
    public static final int MORE = 21;
    /** >= */
    public static final int MOREEQ = 22;
    /** == */
    public static final int EQUAL = 23;
    /** != */
    public static final int NOTEQ = 24;
    /** ( */
    public static final int LP = 25;
    /** ) */
    public static final int RP = 26;
    /** ; */
    public static final int SEMI = 27;
    /** , */
    public static final int COMMA = 28;
    /** { */
    public static final int LBRACE = 29;
    /** } */
    public static final int RBRACE = 30;
    /** // */
    public static final int SINGLECOM = 31;
    /** /* */
    public static final int LEFTMULCOM = 32;
    /** * / */
    public static final int RIGHTMULCOM = 33;
    /** [ */
    public static final int LBRACKET = 34;
    /** ] */
    public static final int RBRACKET = 35;
    /** 标识符 */
    public static final int ID = 36;
    /** int型的值 */
    public static final int INT_NUMBER = 37;
    /** double型的值 */
    public static final int DOUBLE_NUMBER = 38;
    /** String型的字符串 */
    public static final int STRING_TEXT = 39;
    /** error */
    public static final int ERROR = 40;
    /** def */
    public static final int DEF = 42;
    /**return*/
    public static final int RETURN = 43;

    private int type;
    private String value;
    private int lineNo;
    private int position;

/*
    public lexer.Token(int type,String value){
        this.type = type;
        this.value = value;
    }
*/

    public Token(int type, String value, int lineNo, int position){
        this.type = type;
        this.value = value;
        this.lineNo = lineNo;
        this.position = position;

    }
    
    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getPosition() {
        return position;
    }

    public String getType(int i){
        switch (i){
            case 0:
                return "保留字IF";
            case 1:
                return "保留字ELSE";
            case 2:
                return "保留字WHILE";
            case 3:
                return "保留字READ";
            case 4:
                return "保留字WRITE";
            case 5:
                return "保留字INT";
            case 6:
                return "保留字DOUBLE";
            case 7:
                return "保留字BOOL";
            case 8:
                return "保留字TRUE";
            case 9:
                return "保留字FALSE";
            case 10:
                return "保留字STRING";
            case 11:
                return "保留字CHAR";
            case 12:
                return "保留字NULL";
            case 13:
                return "保留字VOID";
            case 41:
                return "保留字BREAK";
            case 14:
                return "运算符ADD";
            case 15:
                return "运算符SUBTRACT";
            case 16:
                return "运算符MUL";
            case 17:
                return "运算符DIV";
            case 18:
                return "赋值ASSIGN";
            case 19:
                return "比较符LESS";
            case 20:
                return "比较符LESSEQ";
            case 21:
                return "比较符MORE";
            case 22:
                return "比较符MOREEQ";
            case 23:
                return "比较符EQUAL";
            case 24:
                return "比较符NOTEQ";
            case 25:
                return "左小括号LP";
            case 26:
                return "右小括号RP";
            case 27:
                return "分号SEMI";
            case 28:
                return "逗号COMMA";
            case 29:
                return "左大括号LBRACE";
            case 30:
                return "右大括号RBRACE";
            case 31:
                return "单行注释SINGLECOM";
            case 32:
                return "多行注释(左)LEFTMULCOM";
            case 33:
                return "多行注释(右)RIGHTMULCOM";
            case 34:
                return "左中括号LBRACKET";
            case 35:
                return "右中括号RBRACKET";
            case 36:
                return "标识符ID";
            case 37:
                return "整型数值INT_NUMBER";
            case 38:
                return "浮点型数值DOUBLE_NUMBER";
            case 39:
                return "字符串文本STRING_TEXT";
            case 40:
                return "错误ERROR";
            case 42:
                return "函数定义 DEF";
            case 43:
                return "返回 RETURN";
        }
        return "";
    }
    //token的输出形式
    @Override
    public String toString() {
        return "line"+lineNo+","+"position"+position+": "+value+"\t\ttype: "+ getType(type);
    }
}