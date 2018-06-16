package frontend;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;

import exceptions.CMMException;
import lexer.FileIO;
import lexer.Lexer;
import lexer.Token;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.theme.SubstanceEbonyTheme;
import parser.Parser;
import semantic.SemanticAnalysis;

/**
 * Created by 79300 on 2017/11/28.
 * GUI
 */

public class MainFrame extends JFrame{
    SyntaxHighlighter syntaxHighlighter;
    /** 界面大小 */
    static final int WIDTH=1600;
    static final int HEIGHT=900;
    /** 菜单栏字体 */
    static Font font1 = new Font("幼圆", Font.PLAIN, 21);
    /** 文本编辑区字体 */
    static Font font2 = new Font("Consolas", Font.PLAIN, 23);
    /** 输出区字体 */
    static Font font3 = new Font("Consolas", Font.PLAIN, 21);


    JFrame frame;
    JPanel panel;
    //按钮栏
    JToolBar bar;
    //菜单栏
    JMenuBar menuBar;

    /** 子菜单栏 */
    JMenuItem newItem;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;
    JMenuItem undoItem;
    JMenuItem redoItem;
    JMenuItem runItem;
    JMenuItem stopItem;
    JMenuItem aboutItem;

    //按钮
    JButton runBtn;
    JButton stopBtn;
    JButton openFileBtn;
    JButton saveFileBtn;
    JButton newFileBtn;
    File file = null;

    /** 输入区 */
    //滚轮
    JScrollPane inPan;
    JPanel inPanel;
    //代码区
    JTextPane editor;
    JPanel inTextPan;
    //行号区
    JList lineNo;
    JPanel lineArea;

    //undo、redo
    UndoManager um;

    //输出区
    JScrollPane outPan;
    //分栏
    JTabbedPane tab;
    TextArea runPanel;
    TextArea lexerPanel;
    TextArea parserPanel;

    public MainFrame() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        frame = new JFrame("CMM解释器");

        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        panel = new JPanel(new BorderLayout());
        frame.setContentPane(panel);
        frame.setJMenuBar(menuBar);

        ImageIcon ii = new ImageIcon(getClass().getResource("/images/icon.png"));
        frame.setIconImage(ii.getImage());

        /** 主菜单栏 */
        JMenu menu1=new JMenu("文件");
        JMenu menu2=new JMenu("编辑");
        JMenu menu3=new JMenu("运行");
        JMenu menu4=new JMenu("帮助");
        Insets insets = new Insets(3,7,7,7);
        menu1.setMargin(insets);
        menu2.setMargin(insets);
        menu3.setMargin(insets);
        menu4.setMargin(insets);
        menu1.setFont(font1);
        menu2.setFont(font1);
        menu3.setFont(font1);
        menu4.setFont(font1);
        menuBar.add(menu1);
        menuBar.add(menu2);
        menuBar.add(menu3);
        menuBar.add(menu4);

        /** 子菜单 */
        newItem = new JMenuItem("新建");
        openItem = new JMenuItem("打开");
        saveItem = new JMenuItem("保存");
        exitItem = new JMenuItem("退出");
        undoItem = new JMenuItem("撤销");
        redoItem = new JMenuItem("重做");
        runItem = new JMenuItem("运行");
        stopItem = new JMenuItem("停止");
        aboutItem = new JMenuItem("关于");
        newItem.setFont(font1);
        openItem.setFont(font1);
        saveItem.setFont(font1);
        exitItem.setFont(font1);
        undoItem.setFont(font1);
        redoItem.setFont(font1);
        runItem.setFont(font1);
        stopItem.setFont(font1);
        aboutItem.setFont(font1);
        menu1.add(newItem);
        menu1.addSeparator();
        menu1.add(openItem);
        menu1.addSeparator();
        menu1.add(saveItem);
        menu1.addSeparator();
        menu1.add(exitItem);
        menu2.add(undoItem);
        menu2.addSeparator();
        menu2.add(redoItem);
        menu3.add(runItem);
        menu3.addSeparator();
        menu3.add(stopItem);
        menu4.add(aboutItem);

        /** 按钮 */
        runBtn = new JButton();
        stopBtn = new JButton();
        openFileBtn = new JButton();
        newFileBtn = new JButton();
        saveFileBtn = new JButton();
        runBtn.setToolTipText("Run");
        stopBtn.setToolTipText("Stop");
        openFileBtn.setToolTipText("Open");
        newFileBtn.setToolTipText("New");
        saveFileBtn.setToolTipText("Save");

        /** 定义按钮大小 */
        runBtn.setBounds(0, 0, 30, 30);
        stopBtn.setBounds(0, 0, 30, 30);
        openFileBtn.setBounds(0, 0, 30, 30);
        newFileBtn.setBounds(0, 0, 30, 30);
        saveFileBtn.setBounds(0, 0, 30, 30);
        Insets insets2 = new Insets(1,1,1,1);
        runBtn.setMargin(insets);
        stopBtn.setMargin(insets);
        openFileBtn.setMargin(insets);
        newFileBtn.setMargin(insets);
        saveFileBtn.setMargin(insets);

        /** 根据按钮大小改变图片大小 */
        ImageIcon ii1 = new ImageIcon(getClass().getResource("/images/run.png"));
        ImageIcon ii3 = new ImageIcon(getClass().getResource("/images/stop.png"));
        ImageIcon ii4 = new ImageIcon(getClass().getResource("/images/new.png"));
        ImageIcon ii5 = new ImageIcon(getClass().getResource("/images/save.png"));
        ImageIcon ii2 = new ImageIcon(getClass().getResource("/images/open.png"));
        Image temp1 = ii1.getImage().getScaledInstance(runBtn.getWidth(), runBtn.getHeight(), ii1.getImage().SCALE_DEFAULT);
        Image temp3 = ii3.getImage().getScaledInstance(stopBtn.getWidth(), stopBtn.getHeight(), ii3.getImage().SCALE_DEFAULT);
        Image temp2 = ii2.getImage().getScaledInstance(openFileBtn.getWidth(), openFileBtn.getHeight(), ii2.getImage().SCALE_DEFAULT);
        Image temp4 = ii4.getImage().getScaledInstance(newFileBtn.getWidth(), newFileBtn.getHeight(), ii4.getImage().SCALE_DEFAULT);
        Image temp5 = ii5.getImage().getScaledInstance(saveFileBtn.getWidth(), saveFileBtn.getHeight(), ii5.getImage().SCALE_DEFAULT);

        /** 定义按钮的图片 */
        runBtn.setIcon(new ImageIcon(temp1));
        stopBtn.setIcon(new ImageIcon(temp3));
        newFileBtn.setIcon(new ImageIcon(temp4));
        saveFileBtn.setIcon(new ImageIcon(temp5));
        openFileBtn.setIcon(new ImageIcon(temp2));

        /** 添加并隔开按钮 */
        bar = new JToolBar();
        Dimension dimension = new Dimension(15,runBtn.getHeight());
        bar.add(runBtn);
        bar.addSeparator(dimension);
        bar.add(stopBtn);
        bar.addSeparator(dimension);
        bar.add(openFileBtn);
        bar.addSeparator(dimension);
        bar.add(newFileBtn);
        bar.addSeparator(dimension);
        bar.add(saveFileBtn);

        /** 输入区 */
        //代码
        editor = new JTextPane();
        editor.setFont(font2);
        inTextPan = new JPanel(new BorderLayout());
        inTextPan.add(editor);
        inPanel = new JPanel(new BorderLayout());
        inPanel.add(BorderLayout.CENTER, inTextPan);
        syntaxHighlighter = new SyntaxHighlighter(editor);
        editor.getDocument().addDocumentListener(syntaxHighlighter);
        //行号
        lineNo = new JList();
        String[] in = new String[10000];
        for(int i=0;i<9999;i++){
            in[i] = String.format("%1$4s", i + 1);
        }
        lineNo = new JList(in);
        lineNo.setFont(new Font("Consolas", Font.PLAIN, 23));
        lineNo.setForeground(Color.GRAY);
        lineArea = new JPanel();
        lineArea.add(lineNo);
        inPanel.add(BorderLayout.WEST, lineArea);
        //滚轮设置
        inPan = new JScrollPane();
        inPan.add(inPanel);
        inPan.setViewportView(inPanel);
        inPan.getVerticalScrollBar().setUnitIncrement(20);

        //输出区
        outPan = new JScrollPane();
        tab = new JTabbedPane();
        runPanel = new TextArea();
        lexerPanel = new TextArea();
        parserPanel = new TextArea();
        runPanel.setEditable(false);
        lexerPanel.setEditable(false);
        parserPanel.setEditable(false);
        //选栏
        tab.setFont(font1);
        tab.add("Run", runPanel);
        tab.add("Lexer", lexerPanel);
        tab.add("Parser", parserPanel);
        tab.setTabPlacement(JTabbedPane.BOTTOM);
        //滚轮设置
        outPan.setViewportView(tab);

        BorderLayout bord = new BorderLayout();
        panel.setLayout(bord);
        panel.add("North",bar);
        panel.add(BorderLayout.CENTER, inPan);
        frame.setVisible(true);
        frame.setSize(WIDTH, HEIGHT);

        um = new UndoManager();
        editor.getDocument().addUndoableEditListener(um);

        /** 菜单栏点击事件 */
        Monitor monitor = new Monitor();
        newItem.addActionListener(monitor);
        openItem.addActionListener(monitor);
        saveItem.addActionListener(monitor);
        exitItem.addActionListener(monitor);
        undoItem.addActionListener(monitor);
        redoItem.addActionListener(monitor);
        runItem.addActionListener(monitor);
        stopItem.addActionListener(monitor);
        aboutItem.addActionListener(monitor);

        /** 按钮点击事件 */
        runBtn.addActionListener(monitor);
        stopBtn.addActionListener(monitor);
        openFileBtn.addActionListener(monitor);
        newFileBtn.addActionListener(monitor);
        saveFileBtn.addActionListener(monitor);
    }

    class Monitor implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            if(o == newItem|o==newFileBtn)
                newFile();
            else if (o == openItem|o==openFileBtn){
                try {
                    openFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if(o == saveItem|o==saveFileBtn){
                try {
                    saveFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if(o == exitItem){
                int n = JOptionPane.showConfirmDialog(null,
                        "确定要退出吗?",
                        "退出",JOptionPane.OK_CANCEL_OPTION);
                if(n == 0)
                    System.exit(0);
            } else if(o == undoItem){
                if(um.canUndo())
                    um.undo();
            } else if(o == redoItem){
                if(um.canRedo())
                    um.redo();
            } else if(o == runItem | o == runBtn){
                run();
            } else if(o == stopItem | o == stopBtn) {
                stop();
            } else if(o == aboutItem)
                JOptionPane.showMessageDialog(null,
                        "CMM解释器1.0\nCopyright@2017 \nAll rights reserved.",
                        "关于", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void newFile(){
        if(!editor.getText().equals("")){
            int n = JOptionPane.showConfirmDialog(null,
                    "是否清空?",
                    "CMM解释器",JOptionPane.YES_NO_CANCEL_OPTION);
            if(n == 0){
                editor.setText("");
            }
        }
    }

    private void openFile() throws IOException {
        JFileChooser jFileChooser = new JFileChooser();
//        jFileChooser.showOpenDialog(null);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.setCurrentDirectory(new File("."));
        String fileName = null;
        String filenameEnd = ".cmm";
        jFileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File file) {
                return true;
            }

            @Override
            public String getDescription() {
                return "All files";
            }
        });
        jFileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File file) {
                return file.getName().endsWith(filenameEnd) || file.isDirectory();
            }
            public String getDescription() {
                return filenameEnd;
            }
        });
        int option=jFileChooser.showDialog(null, null);
        if(option==JFileChooser.APPROVE_OPTION) {
            editor.setText("");
            File selectedFile = jFileChooser.getSelectedFile();
            String readString = FileIO.read(selectedFile.getAbsolutePath());
            try {
                file = selectedFile;
                editor.getDocument().removeDocumentListener(syntaxHighlighter);
                syntaxHighlighter = new SyntaxHighlighter(editor);
                editor.getDocument().addDocumentListener(syntaxHighlighter);
                editor.getDocument().insertString(0, readString, null);
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void saveFile() throws IOException {
        if(editor.getText().equals(""))
            JOptionPane.showMessageDialog(null,
                    "文件内容不能为空!",
                    "错误", JOptionPane.ERROR_MESSAGE);
        else {
            String content = editor.getText();
            if(file!=null){
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write(content);
                    bufferedWriter.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }else {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
                int option = fileChooser.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file.exists()) {
                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                            bufferedWriter.write(content);
                            bufferedWriter.close();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            file.createNewFile();
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                            bufferedWriter.write(content);
                            bufferedWriter.close();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void run(){
        if(editor.getText().equals("")) {
            runPanel.setText("");
            lexerPanel.setText("");
            parserPanel.setText("");
        }
        else {
            boolean isLexer = false;
            try{
                Lexer lexer = new Lexer(editor.getText());
                lexer.runLexer();
                StringBuilder tokenString = new StringBuilder();
                for(Token token:lexer.getTokens()){
                    tokenString.append(token.toString()).append("\n");
                }
                String errors = lexer.getErrorsString();
                if(errors!=null&&!errors.isEmpty()){
                    tokenString.append(errors);
                    lexerPanel.setText(tokenString.toString());
                    lexerPanel.setForeground(new Color(0x000000));
                    runPanel.setText(errors);
                    runPanel.setForeground(new Color(0xEE2C2C));
                    isLexer = true;
                }
                lexerPanel.setText(tokenString.toString());
                lexerPanel.setForeground(new Color(0x000000));
                Parser parser = new Parser(editor.getText());
                parser.run();
                parserPanel.setText(parser.getParserResult());
                parserPanel.setForeground(new Color(0x000000));
                SemanticAnalysis analysis = new SemanticAnalysis(parser.getProgram());
                analysis.run();
                runPanel.setText(analysis.getResult());
                runPanel.setForeground(new Color(0x000000));
            } catch (IOException e) {
                runPanel.setText(e.getMessage());
                runPanel.setForeground(new Color(0xEE2C2C));
            } catch (CMMException e) {
                if(isLexer){
                }else{
                    Style errorStyle = ((StyledDocument) editor.getDocument()).addStyle("error_Style", null);
                    runPanel.setText(e.getMessage());
                    runPanel.setForeground(new Color(0xEE2C2C));
                }
            }
        }

        panel.add(BorderLayout.CENTER, inPan);
        panel.add(BorderLayout.SOUTH, outPan);

        panel.updateUI();
    }

    private void stop(){
        panel.remove(outPan);
        panel.add(BorderLayout.CENTER, inPan);
        panel.updateUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel");
                    UIManager.setLookAndFeel(new SubstanceLookAndFeel());
                    JFrame.setDefaultLookAndFeelDecorated(true);//设置窗口
                    JDialog.setDefaultLookAndFeelDecorated(true);//设置对话框
                    SubstanceLookAndFeel.setCurrentTheme(new SubstanceEbonyTheme());//设置主题
                    new MainFrame();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Something went wrong!");
                }
            }
        });
    }
}

/**
 * 当文本输入区的有字符插入或者删除时, 进行高亮.
 * 新增错误提示，当每行代码缺失逗号或大括号时标红
 */
class SyntaxHighlighter implements DocumentListener {
    private Set<String> keywords;
    private Style keywordStyle;
    private Style keywordStyle2;
    private Style numberStyle;
    private Style normalStyle;
    private Style noSemiError;
    private Style commentStyle;
    private int isSingleLineCommExist = 0;
    private int isMultilineCommExist = 0;
    private int singleLineCommLoc = 0;
    private int multilineCommLoc = 0;

    public SyntaxHighlighter(JTextPane editor) {
        // 准备着色使用的样式
        keywordStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
        keywordStyle2 = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style2", null);
        numberStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
        normalStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
        noSemiError = ((StyledDocument) editor.getDocument()).addStyle("noSemi_Style", null);
        commentStyle = ((StyledDocument) editor.getDocument()).addStyle("comment_Style", null);

        StyleConstants.setForeground(keywordStyle, new Color(0xD2691E));
        StyleConstants.setForeground(keywordStyle2, new Color(0xb22222));
        StyleConstants.setForeground(numberStyle, new Color(0x4F94CD));
        StyleConstants.setForeground(normalStyle, Color.BLACK);
        StyleConstants.setUnderline(noSemiError, true);
        StyleConstants.setForeground(noSemiError, Color.RED);
        StyleConstants.setForeground(commentStyle, new Color(0x71C671));
        StyleConstants.setItalic(commentStyle, true);

        // 准备关键字
        keywords = new HashSet<String>();
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("read");
        keywords.add("write");
        keywords.add("int");
        keywords.add("double");
        keywords.add("string");
        keywords.add("bool");
        keywords.add("def");
        keywords.add("return");
        keywords.add("true");
        keywords.add("false");
    }

    public void colouring(StyledDocument doc, int pos, int len) throws BadLocationException {
        // 取得插入或者删除后影响到的单词.
        int start = indexOfWordStart(doc, pos);
        int end = indexOfWordEnd(doc, pos + len);

        char ch;
        while (start < end) {
            ch = getCharAt(doc, start);
            if(ch == '*'){
                if(isMultilineCommExist == 2)
                    coloringComment(doc, start, 2);
                else if(isSingleLineCommExist == 1)
                    coloringComment(doc, start, 1);
                else if(start != 0 && getCharAt(doc, start-1) == '/'){
                    multilineCommLoc = start - 1;
                    isMultilineCommExist = 2;
                    coloringComment(doc, start, 2);
                }
                ++start;
            } else if(ch == '/'){
                if(isMultilineCommExist == 2){
                    coloringComment(doc, start, 2);
                    if(start != 0 && getCharAt(doc, start-1) == '*'){
                        isMultilineCommExist = 1;
                    }
                } else if(isSingleLineCommExist == 1){
                    coloringComment(doc, start, 1);
                } else if(isSingleLineCommExist == 0 && start != 0 && getCharAt(doc, start-1) == '/'){
                    singleLineCommLoc = start - 1;
                    isSingleLineCommExist = 1;
                    coloringComment(doc, start, 1);
                }
                ++start;
            } else if (Character.isLetter(ch) || ch == '_') {
                if(isMultilineCommExist == 2){
                    coloringComment(doc, start, 2);
                    ++start;
                } else if(isSingleLineCommExist == 1){
                    coloringComment(doc, start, 1);
                    ++start;
                } else
                    // 如果是以字母或者下划线开头, 说明是单词
                    // pos为处理后的最后一个下标
                    start = colouringWord(doc, start);
            } else if(Character.isDigit(ch)){
                if(isMultilineCommExist == 2){
                    coloringComment(doc, start, 2);
                    ++start;
                }
                else if(isSingleLineCommExist == 1){
                    coloringComment(doc, start, 1);
                    ++start;
                }
                else
                    // 如果是以数字开头，说明有可能是数字
                    // pos为处理后的最后一个下标
                    start = colouringNumber(doc, start);
            } else if(ch == '\n' && start != 0){
                if(isSingleLineCommExist == 0 && (isMultilineCommExist == 0 | isMultilineCommExist == 1)){
                    //碰到换行时检查是否缺失逗号
                    int i = 1;
                    while(getCharAt(doc, start - i) == ' ' && i < start)
                        i++;
                    if(isMultilineCommExist == 0){
                        if(getCharAt(doc, start - i) != ';'
                                && getCharAt(doc, start - i) != '}'
                                && getCharAt(doc, start - i) != '{'
                                && getCharAt(doc, start - i) != '\n'){
                            SwingUtilities.invokeLater(new ColouringTask(doc, start - i, 1, noSemiError));
                        } else {

                            SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                        }
                    }else if(isMultilineCommExist == 1){
                        if(getCharAt(doc, start - i) != ';'
                                && getCharAt(doc, start - i) != '}'
                                && getCharAt(doc, start - i) != '{'
                                && getCharAt(doc, start - i) != '\n'
                                && !(getCharAt(doc, start - i) == '/' && getCharAt(doc, start - i - 1) == '*')){
                            SwingUtilities.invokeLater(new ColouringTask(doc, start - i, 1, noSemiError));
                        } else {
                            SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                        }
                        isMultilineCommExist = 0;
                    }
                } else{
                    SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, commentStyle));
                }

                ++start;
                isSingleLineCommExist = 0;
            } else {
                SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                ++start;
            }
        }
    }

    public void coloringComment(StyledDocument doc, int pos, int type) throws BadLocationException {
        switch(type){
            case 1:
                SwingUtilities.invokeLater(new ColouringTask(doc, singleLineCommLoc, pos - singleLineCommLoc + 1, commentStyle));
                return;
            case 2:
                SwingUtilities.invokeLater(new ColouringTask(doc, multilineCommLoc, pos - multilineCommLoc + 1, commentStyle));
        }
    }

    /**
     * 判断单词是否为关键词并对其进行着色, 返回单词结束的下标.
     * @param doc
     * @param pos
     * @return
     * @throws BadLocationException
     */
    public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
        int wordEnd = indexOfWordEnd(doc, pos);
        String word = doc.getText(pos, wordEnd - pos);

        if (keywords.contains(word)) {
            // 如果是关键字, 就进行关键字的着色, 否则使用普通的着色.
            if(word.equals("def")){
                SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordStyle2));
            }else
                SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordStyle));
        } else{
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));
        }
        return wordEnd;
    }

    /**
     * 判断单词是否为数字并对其进行着色, 返回单词结束的下标.
     * @param doc
     * @param pos
     * @return
     * @throws BadLocationException
     */
    public int colouringNumber(StyledDocument doc, int pos) throws BadLocationException {
        int wordEnd = indexOfWordEnd(doc, pos);
        String word = doc.getText(pos, wordEnd - pos);
        Pattern pattern = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
//        Pattern numberPattern = Pattern.compile("((-)?([1-9][0-9]+)|[0-9]((\\.)[0-9]*)?)|0x[0-9A-Fa-f]+");
        if (pattern.matcher(word).matches()) {
            // 如果是数字, 就进行数字的着色, 否则使用普通的着色.
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, numberStyle));
        } else{
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));
        }
        return wordEnd;
    }

    /**
     * 取得在文档中下标在pos处的字符.
     * 如果pos为doc.getLength(), 返回的是一个文档的结束符, 不会抛出异常. 如果pos<0, 则会抛出异常.
     * 所以pos的有效值是[0, doc.getLength()]
     * @param doc
     * @param pos
     * @return
     * @throws BadLocationException
     */
    public char getCharAt(Document doc, int pos) throws BadLocationException {
        return doc.getText(pos, 1).charAt(0);
    }

    /**
     * 取得下标为pos时, 它所在的单词开始的下标. ?±wor^d?± (^表示pos, ?±表示开始或结束的下标)
     * @param doc
     * @param pos
     * @return
     * @throws BadLocationException
     */
    public int indexOfWordStart(Document doc, int pos) throws BadLocationException {
        // ´Ópos¿ªÊ¼ÏòÇ°ÕÒµ½µÚÒ»¸ö·Çµ¥´Ê×Ö·û.
        for (; pos > 0 && isWordCharacter(doc, pos - 1); --pos);
        return pos;
    }

    /**
     * 取得下标为pos时, 它所在的单词结束的下标. ?±wor^d?± (^表示pos, ?±表示开始或结束的下标)
     * @param doc
     * @param pos
     * @return
     * @throws BadLocationException
     */
    public int indexOfWordEnd(Document doc, int pos) throws BadLocationException {
        // ´Ópos¿ªÊ¼ÏòÇ°ÕÒµ½µÚÒ»¸ö·Çµ¥´Ê×Ö·û.
        for (; isWordCharacter(doc, pos); ++pos);
        return pos;
    }

    /**
     * 如果一个字符是字母, 数字, 下划线, 则返回true.
     * @param doc
     * @param pos
     * @return
     * @throws BadLocationException
     */
    public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {
        char ch = getCharAt(doc, pos);
        if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') { return true; }
        return false;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 完成着色任务
     */
    private class ColouringTask implements Runnable {
        private StyledDocument doc;
        private Style style;
        private int pos;
        private int len;

        public ColouringTask(StyledDocument doc, int pos, int len, Style style) {
            this.doc = doc;
            this.pos = pos;
            this.len = len;
            this.style = style;
        }

        public void run() {
            try {
                // 对字符进行着色
                doc.setCharacterAttributes(pos, len, style, true);
            } catch (Exception e) {
            }
        }
    }
}