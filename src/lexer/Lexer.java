package lexer;

import java.util.ArrayList;

/**
 * Created by 79300 on 2017/10/8.
 */
public class Lexer {
    //声明要读取的字符串，当前字符和之后的几个字符
    String inputText;
    char currentChar;
    char nextChar1='\0';
    char nextChar2='\0';
    char nextChar3='\0';
    char nextChar4='\0';
    char nextChar5='\0';
    char nextChar6='\0';
    //charNo表示当前字符的序号
    int charNo=0;
    //行号和位置及初始值
    int lineNo=1;
    int position=0;
    //两个ArrayList存储分析出的token序列和错误token序列
    ArrayList<Token> tokens = new ArrayList<>();
    private ArrayList<Token> errors = new ArrayList<>();

    public String getErrorsString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(Token token:errors){
            stringBuilder.append("error: ").append(token.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
    //初始化，currentChar指向文本中第一个字符
    public Lexer(String inputText){
        this.inputText=inputText;
        currentChar=inputText.charAt(charNo);
        nextChar1=getNextChar(charNo+1);
        nextChar2=getNextChar(charNo+2);
        nextChar3=getNextChar(charNo+3);
        nextChar4=getNextChar(charNo+4);
        nextChar5=getNextChar(charNo+5);
        nextChar6=getNextChar(charNo+6);

    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<Token> getErrors() {
        return errors;
    }

    public void run(){
        runLexer();
        System.out.println("词法分析开始");
        for(Token token:tokens){
            System.out.println(token.toString());
        }
        System.out.println();
        if(!errors.isEmpty()){
            for(Token token:errors){
                System.out.println("error: "+token.toString());
            }
        }
    }

    public void runLexer(){
        while(currentChar!='\0'){
            /**忽略空格、换行、回车和TAB键*/
            if(isSpace(currentChar)){
                move();
                continue;
            }
            /**识别保留字*/
            if(currentChar=='i'&&nextChar1=='f'&&(nextChar2=='\0'||nextChar2=='('||isSpace(nextChar2))){
                tokens.add(new Token(Token.IF,"if",lineNo,position));
                move(2);
                continue;
            }
            if(currentChar=='e'&&nextChar1=='l'&&nextChar2=='s'&&nextChar3=='e'&&(nextChar4=='\0'||nextChar4=='{'||isSpace(nextChar4))){
                tokens.add(new Token(Token.ELSE,"else",lineNo,position));
                move(4);
                continue;
            }
            if (currentChar=='w'&&nextChar1=='h'&&nextChar2=='i'&&nextChar3=='l'&&nextChar4=='e'&&(nextChar5=='\0'||nextChar5=='('||isSpace(nextChar5))){
                tokens.add(new Token(Token.WHILE,"while",lineNo,position));
                move(5);
                continue;
            }
            if(currentChar=='c'&&nextChar1=='h'&&nextChar2=='a'&&nextChar3=='r'&&(nextChar4=='\0'||isSpace(nextChar4))){
                tokens.add(new Token(Token.CHAR,"char",lineNo,position));
                move(4);
                continue;
            }
            if(currentChar=='r'&&nextChar1=='e'&&nextChar2=='a'&&nextChar3=='d'&&(nextChar4=='\0'||nextChar4=='('||isSpace(nextChar4))){
                tokens.add(new Token(Token.READ,"read",lineNo,position));
                move(4);
                continue;
            }
            if(currentChar=='w'&&nextChar1=='r'&&nextChar2=='i'&&nextChar3=='t'&&nextChar4=='e'&&(nextChar5=='\0'||nextChar5=='('||isSpace(nextChar5))){
                tokens.add(new Token(Token.WRITE,"write",lineNo,position));
                move(5);
                continue;
            }
            if(currentChar=='i'&&nextChar1=='n'&&nextChar2=='t'&&(nextChar3=='\0'||isSpace(nextChar3))){
                tokens.add(new Token(Token.INT,"int",lineNo,position));
                move(3);
                continue;
            }
            if(currentChar=='d'&&nextChar1=='o'&&nextChar2=='u'&&nextChar3=='b'&&nextChar4=='l'&&nextChar5=='e'&&(nextChar6=='\0'||isSpace(nextChar6))){
                tokens.add(new Token(Token.DOUBLE,"double",lineNo,position));
                move(6);
                continue;
            }
            if(currentChar=='s'&&nextChar1=='t'&&nextChar2=='r'&&nextChar3=='i'&&nextChar4=='n'&&nextChar5=='g'&&(nextChar6=='\0'||isSpace(nextChar6))){
                tokens.add(new Token(Token.STRING,"string",lineNo,position));
                move(6);
                continue;
            }
            if(currentChar=='b'&&nextChar1=='o'&&nextChar2=='o'&&nextChar3=='l'&&(nextChar4=='\0'||isSpace(nextChar4))){
                tokens.add(new Token(Token.BOOL,"bool",lineNo,position));
                move(4);
                continue;
            }
            if(currentChar=='t'&&nextChar1=='r'&&nextChar2=='u'&&nextChar3=='e'&&(nextChar4=='\0'||isFollow(nextChar4))){
                tokens.add(new Token(Token.TRUE,"true",lineNo,position));
                move(4);
                continue;
            }
            if(currentChar=='f'&&nextChar1=='a'&&nextChar2=='l'&&nextChar3=='s'&&nextChar4=='e'&&(nextChar5=='\0'||isFollow(nextChar5))){
                tokens.add(new Token(Token.FALSE,"false",lineNo,position));
                move(5);
                continue;
            }
            if(currentChar=='n'&&nextChar1=='u'&&nextChar2=='l'&&nextChar3=='l'&&(nextChar4=='\0'||isFollow(nextChar4))){
                tokens.add(new Token(Token.NULL,"null",lineNo,position));
                move(4);
                continue;
            }
            if(currentChar=='v'&&nextChar1=='o'&&nextChar2=='i'&&nextChar3=='d'&&(nextChar4=='\0'||isSpace(nextChar4))){
                tokens.add(new Token(Token.VOID,"void",lineNo,position));
                move(4);
                continue;
            }
            if(currentChar=='b'&&nextChar1=='r'&&nextChar2=='e'&&nextChar3=='a'&&nextChar4=='k'&&(nextChar5=='\0'||isSpace(nextChar5)||nextChar5==';')){
                tokens.add(new Token(Token.BREAK,"break",lineNo,position));
                move(5);
                continue;
            }

            /** def 函数定义头*/
            if(currentChar=='d'&&nextChar1=='e'&&nextChar2=='f'&&(nextChar3=='\0'||isSpace(nextChar3))){
                tokens.add(new Token(Token.DEF,"def",lineNo,position));
                move(3);
                continue;
            }
            /**return */
            if(currentChar=='r'&&nextChar1=='e'&&nextChar2=='t'&&nextChar3=='u'&&nextChar4=='r'&&nextChar5=='n'&&(nextChar6=='\0'||isSpace(nextChar6)||nextChar6==';')){
                tokens.add(new Token(Token.RETURN, "return",lineNo,position));
                move(6);
                continue;
            }


            /** + */
            if(currentChar=='+'){
                tokens.add(new Token(Token.ADD,"+",lineNo,position));
                move();
                continue;
            }

            /** - */
            if(currentChar=='-'&&!isNumber(nextChar1)){
                tokens.add(new Token(Token.SUBTRACT,"-",lineNo,position));
                move();
                continue;
            }

            /** * */
            if(currentChar=='*'&&nextChar1!='/'){
                tokens.add(new Token(Token.MUL,"*",lineNo,position));
                move();
                continue;
            }

            /** / */
            if(currentChar=='/'&&nextChar1!='/'&&nextChar1!='*'){
                tokens.add(new Token(Token.DIV,"/",lineNo,position));
                move();
                continue;
            }

            /** = */
            if(currentChar=='='&&nextChar1!='='){
                tokens.add(new Token(Token.ASSIGN,"=",lineNo,position));
                move();
                continue;
            }

            /** < */
            if(currentChar=='<'&&nextChar1!='='){
                tokens.add(new Token(Token.LESS,"<",lineNo,position));
                move();
                continue;
            }

            /** <= */
            if(currentChar=='<'&&nextChar1=='='){
                tokens.add(new Token(Token.LESSEQ,"<=",lineNo,position));
                move(2);
                continue;
            }

            /** > */
            if(currentChar=='>'&&nextChar1!='='){
                tokens.add(new Token(Token.MORE,">",lineNo,position));
                move();
                continue;
            }

            /** >= */
            if(currentChar=='>'&&nextChar1=='='){
                tokens.add(new Token(Token.MOREEQ,">=",lineNo,position));
                move(2);
                continue;
            }

            /** == */
            if(currentChar=='='&&nextChar1=='='){
                tokens.add(new Token(Token.EQUAL,"==",lineNo,position));
                move(2);
                continue;
            }

            /** != */
            if(currentChar=='!'&&nextChar1=='='){
                tokens.add(new Token(Token.NOTEQ,"!=",lineNo,position));
                move(2);
                continue;
            }

            /** ( */
            if(currentChar=='('){
                tokens.add(new Token(Token.LP,"(",lineNo,position));
                move();
                continue;
            }

            /** ) */
            if(currentChar==')'){
                tokens.add(new Token(Token.RP,")",lineNo,position));
                move();
                continue;
            }

            /** " */
            if(currentChar=='"'){
                StringBuilder tempsb = new StringBuilder();
                tempsb.append('"');
                move();
                while (currentChar!='"'){
                    tempsb.append(currentChar);
                    move();
                    if(currentChar=='"') tempsb.append('"');
                    if(charNo==inputText.length()-1&&currentChar!='"'){
                        errors.add(new Token(Token.ERROR,"Expect \".",lineNo,position));
                        break;
                    }
                }
                tokens.add(new Token(Token.STRING_TEXT,tempsb.toString(),lineNo,position));
                move();
                continue;
            }

            int _a = 1;

            /** ; */
            if(currentChar==';'){
                tokens.add(new Token(Token.SEMI,";",lineNo,position));
                move();
                continue;
            }

            /** , */
            if(currentChar==','){
                tokens.add(new Token(Token.COMMA,",",lineNo,position));
                move();
                continue;
            }

            /** { */
            if(currentChar=='{'){
                tokens.add(new Token(Token.LBRACE,"{",lineNo,position));
                move();
                continue;
            }


            /** } */
            if(currentChar=='}'){
                tokens.add(new Token(Token.RBRACE,"}",lineNo,position));
                move();
                continue;
            }


            /** [ */
            if(currentChar=='['){
                tokens.add(new Token(Token.LBRACKET,"[",lineNo,position));
                move();
                continue;
            }
            /** ] */
            if(currentChar==']'){
                tokens.add(new Token(Token.RBRACKET,"]",lineNo,position));
                move();
                continue;
            }

            /** // */
            if(currentChar=='/'&&nextChar1=='/'){
//                tokens.add(new Token(Token.SINGLECOM,"//",lineNo,position+1));
                while(currentChar!='\n'){
                    move();
                    if(currentChar=='\0'&&nextChar1=='\0'){
                        break;
                    }
                }
                move();
                continue;
            }

            /**识别标识符*/
            //标识符以字母开头
            if(isLetter(currentChar)){
                StringBuilder tempsb = new StringBuilder();
                //读入第一个字母
                tempsb.append(currentChar);
                //之后只要是字母或数字或下划线都加到stringbuilder的最后
                while(isLetter(nextChar1)||isNumber(nextChar1)||nextChar1=='_'){
                    tempsb.append(nextChar1);
                    move();
                }
                //检验stringbuilder是否以_结尾，是则将序列存到errors中，否则将序列存入tokens中。
                if(tempsb.toString().endsWith("_")){
                    errors.add(new Token(Token.ERROR,"ID "+tempsb.toString()+" can't end with \'_\'",lineNo,position));
                }else{
                    tokens.add(new Token(Token.ID,tempsb.toString(),lineNo,position));
                }
                move();
                continue;
            }
            //报错以数字开头的标识符
            if(isNumber(currentChar)&&(isLetter(nextChar1)||isNumber(nextChar1)||nextChar1=='_')){
                StringBuilder tempsb = new StringBuilder();
                //读入第一个数字
                tempsb.append(currentChar);
                //之后只要是数字、字母或者下划线都加到stringbuilder后
                while (isNumber(nextChar1)||isLetter(nextChar1)||nextChar1=='_'||nextChar1=='.'){
                    tempsb.append(nextChar1);
                    move();
                }
                //检验stringbuilder是否全部为数字，是则加入tokens，不是则加入error报错
                if(tempsb.toString().matches("[0-9]+")){
                    tokens.add(new Token(Token.INT_NUMBER,tempsb.toString(),lineNo,position));
                }else if(tempsb.toString().matches("[0-9]+(\\.)?[0-9]*")){
                    tokens.add(new Token(Token.DOUBLE_NUMBER,tempsb.toString(),lineNo, position));
                }else if(tempsb.toString().matches("0[xX][a-fA-f0-9]+")){
                    tokens.add(new Token(Token.INT_NUMBER,tempsb.toString(),lineNo,position));
                }
                else {
                    errors.add(new Token(Token.ERROR,"ID "+tempsb.toString()+" can't begin with number",lineNo,position));
                }
                move();
                continue;
            }
            //报错以_开头的标识符
            if(currentChar=='_'&&(isLetter(nextChar1)||isNumber(nextChar1)||nextChar1=='_')){
                StringBuilder tempsb = new StringBuilder();
                //读入下划线
                tempsb.append(currentChar);
                //之后只要是数字、字母或者下划线都加到stringbuilder后
                while (isNumber(nextChar1)||isLetter(nextChar1)||nextChar1=='_'){
                    tempsb.append(nextChar1);
                    move();
                }
                //以_开头的标识符加入error
                errors.add(new Token(Token.ERROR,"ID "+tempsb.toString()+ " can't begin with \'_\'",lineNo,position));
                move();
                continue;
            }

            /**识别十六进制数字*/
            if(currentChar=='0'&&(nextChar1=='x'||nextChar1=='X')){
                StringBuilder tempsb = new StringBuilder();
                tempsb.append(currentChar);
                tempsb.append(nextChar1);
                move(2);
                while(isHex(currentChar)){
                    tempsb.append(currentChar);
                    move();
                }
                tokens.add(new Token(Token.INT_NUMBER,tempsb.toString(),lineNo,position));
                continue;
            }

            /**识别十进制数字*/
            //识别负数
            if((currentChar == '-' && isNumber(nextChar1))){
                StringBuilder tempsb = new StringBuilder();
                tempsb.append(currentChar);
                move();
                buildNumber(tempsb);
                continue;
            //识别正数
            }else if(isNumber(currentChar)){
                StringBuilder tempsb = new StringBuilder();
                buildNumber(tempsb);
                continue;
            }


            /** /* */
            if(currentChar=='/'&&nextChar1=='*'){
//                tokens.add(new Token(Token.LEFTMULCOM,"/*",lineNo,position+2));
                move();
                while (true){
                    move();
                    //匹配右多行注释，存入token并移动
                    if(currentChar=='*'&&nextChar1=='/') {
//                        tokens.add(new Token(Token.RIGHTMULCOM,"*/",lineNo,position+1));
                        move(2);
                        break;
                    }
                    if(charNo>inputText.length()-1&&currentChar=='\0'){
                        errors.add(new Token(Token.ERROR,"Expect */.",lineNo,position+1));
                        break;
                    }
                }
            //报错没有被匹配到的*/
            }else if(currentChar=='*'&&nextChar1=='/'){
                errors.add(new Token(Token.ERROR,"Can't match */ with /*",lineNo,position));
                move(2);
                continue;
            }

            else {
                /** 以上都不匹配，添加到error */
                errors.add(new Token(Token.ERROR,"Can't match symbol "+currentChar,lineNo,position));
                move();
                /** 到结尾时结束循环 */
                if(charNo==inputText.length()){
                    break;
                }
            }


        }
    }

    //移动i位
    private void move(int i){
        for(int j=0;j<i;j++)
            move();
    }
    //移动一位
    private void move(){
        //currentChar和nextChar移动到下一个字符,position后移
        charNo++;
        nextChar1=getNextChar(charNo+1);
        nextChar2=getNextChar(charNo+2);
        nextChar3=getNextChar(charNo+3);
        nextChar4=getNextChar(charNo+4);
        nextChar5=getNextChar(charNo+5);
        nextChar6=getNextChar(charNo+6);
        if(charNo >= inputText.length()){
            currentChar = '\0';
        }else{
            currentChar = inputText.charAt(charNo);
        }
        position++;
        //换行改变行号，重置位置
        if(currentChar == '\n'){
            lineNo++;
            position = 0;
        }
    }
    //是否为回车、换行、tab键或空格
    private boolean isSpace(char inputChar){
        return inputChar == '\n' || inputChar == '\t' || inputChar == '\r' || inputChar == ' ';
    }
    //能否跟在true和false后
    private boolean isFollow(char inputChar){
        return inputChar == ')' || inputChar == ';' || inputChar == '&' || inputChar == ',' || inputChar == '|' || isSpace(inputChar);
    }
    //是否为字母
    private boolean isLetter(char inputChar){
        return inputChar >= 'a' && inputChar <= 'z' || (inputChar >= 'A' && inputChar <= 'Z');
    }
    //是否为数字
    private boolean isNumber(char inputChar){
        return inputChar >= '0' && inputChar <= '9';
    }
    //是否为十六进制数字
    private boolean isHex(char inputChar){
        return isNumber(inputChar)||(inputChar>='a'&&inputChar<='e')||(inputChar>='A'&&inputChar<='E');
    }
    //构造十进制数字
    private void buildNumber(StringBuilder sb){
        //当前字符是数字
        if(isNumber(currentChar)) {
            //设置bool值处理有两个.的情况
            boolean isDouble = false;
            //把第一个数字加进sb
            sb.append(currentChar);
            //把后面的数字和一个点加入到sb,如果遇到第二个.就退出循环。
            while (nextChar1 == '.' || isNumber(nextChar1)) {
                if (nextChar1 == '.') {
                    if (isDouble) {
                        break;
                    } else isDouble = true;
                }
                sb.append(nextChar1);
                move();
            }
            //识别浮点数，如1..123，首先识别的1.为正确的token，之后移动使currentChar为第二个小数点，重新遍历后报错
            if (isDouble)
                tokens.add(new Token(Token.DOUBLE_NUMBER, sb.toString(), lineNo, position));
            //识别整数，09、-09的int情况加入errors
            else if ((sb.toString().matches("(-)?([1-9][0-9]*)|0") && !isDouble)) {
                tokens.add(new Token(Token.INT_NUMBER, sb.toString(), lineNo, position));
            } else {
                errors.add(new Token(Token.ERROR, sb.toString(), lineNo, position));
            }
            move();
        }
    }
    //得到下一个字符
    private char getNextChar(int length){
        if(length>=inputText.length()){
            return '\0';
        }
        return inputText.charAt(length);
    }
    private boolean isX(char inputChar){
        return inputChar=='x'||inputChar=='X';
    }
}
