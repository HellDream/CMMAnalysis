package frontend;

import exceptions.CMMException;
import lexer.FileIO;
import parser.Parser;
import semantic.SemanticAnalysis;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Objects;

/**
 * Created by Yu on 2017/12/3.
 */
public class CMMApp {
    private JFrame frmCmm;
    private JMenuBar menuBar;
    private JTextPane textPane;
    private JScrollPane scrollPane_1,scrollPane_2,scrollPane_3;
    private JTextArea textAreaConsole,textAreaLexer,textAreaParser;
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        CMMApp cmmFront = new CMMApp();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.updateComponentTreeUI(cmmFront.frmCmm);
        cmmFront.frmCmm.setVisible(true);
    }
    public CMMApp() {
        initialize();
    }

    private void initialize(){
        frmCmm = new JFrame();
        frmCmm.setResizable(false);

        frmCmm.setTitle("CMM Interpreter");
        frmCmm.setBounds(100, 100, 1000, 800);
        frmCmm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frmCmm.getContentPane().setLayout(null);
        generateMenuBar();
        frmCmm.getContentPane().add(menuBar);
        generateCodeTextArea();
        generateConsoleArea();
    }

    private void generateConsoleArea() {
        Font font = new Font("Consolas", Font.BOLD, 14);
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(10, 458, 980, 300);
        frmCmm.getContentPane().add(tabbedPane);
        scrollPane_1 = new JScrollPane();
        /*Console 窗口*/
        tabbedPane.addTab("Console", null, scrollPane_1, null);

        textAreaConsole = new JTextArea();
        textAreaConsole.setFont(font);
        textAreaConsole.setEditable(false);
        scrollPane_1.setViewportView(textAreaConsole);

        scrollPane_2 = new JScrollPane();
        /*词法分析窗口*/
        tabbedPane.addTab("词法分析", null, scrollPane_2, null);

        textAreaLexer = new JTextArea();
        textAreaLexer.setFont(font);
        textAreaLexer.setEditable(false);
        textAreaLexer.setText("");
        scrollPane_3 = new JScrollPane();
        /*语法分析窗口*/
        tabbedPane.addTab("语法分析", null, scrollPane_3, null);
        textAreaParser = new JTextArea();
        textAreaParser.setFont(font);
        textAreaParser.setEditable(false);
        textAreaParser.setText("");
        scrollPane_2.setViewportView(textAreaLexer);
        scrollPane_3.setViewportView(textAreaParser);
    }

    private void generateCodeTextArea() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 46, 980, 402);
        textPane = new JTextPane();
        Font font = new Font("Consolas", Font.BOLD, 14);
        textPane.setFont(font);
        scrollPane.setViewportView(textPane);
        frmCmm.getContentPane().add(scrollPane);
    }

    private void generateMenuBar(){
        menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 1000, 21);
        JMenu fileMenu = new JMenu("File(F)");
        menuBar.add(fileMenu);
        JMenuItem newFileMenu = new JMenuItem("New File");
        fileMenu.add(newFileMenu);
        //新建文件监听
        newFileMenu.addActionListener(e -> textPane.setText(""));
        JMenuItem openFile = new JMenuItem("Open File");
        fileMenu.add(openFile);
        //打开文件监听
        openFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(new File("."));
            String filenameEnd = ".cmm";
            fileChooser.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File file) {
                    return true;
                }

                @Override
                public String getDescription() {
                    return "All files";
                }
            });
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File file) {
                    return file.getName().endsWith(filenameEnd) || file.isDirectory();
                }
                public String getDescription() {
                    return filenameEnd;
                }
            });
            int option=fileChooser.showDialog(null, null);
            if(option==JFileChooser.APPROVE_OPTION){
                textPane.setText("");
                File selectedFile = fileChooser.getSelectedFile();
                String readString = FileIO.read(selectedFile.getAbsolutePath());
                try {
                    textPane.getDocument().insertString(0,readString,null);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JMenuItem saveFileMenu = new JMenuItem("Save(S)");
        fileMenu.add(saveFileMenu);
        //保存文件监听
        saveFileMenu.addActionListener(e -> {
            JFileChooser fileChooser=new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            int option=fileChooser.showSaveDialog(null);
            if (option== JFileChooser.APPROVE_OPTION) {
                String content=textPane.getText();
                File file=fileChooser.getSelectedFile();
                if (file.exists()) {
                    //文件存在
                    try {
                        BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
                        bufferedWriter.write(content);
                        bufferedWriter.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }else {
                    //文件不存在
                    try {
                        file.createNewFile();
                        BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
                        bufferedWriter.write(content);
                        bufferedWriter.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }

                System.out.println(file.getAbsolutePath()+"--------"+file.getName());

            }else if (option== JFileChooser.CANCEL_OPTION) {
                System.out.println("取消保存");
            }else {
                //出错
            }
        });
        JMenu runMenu = new JMenu("Run(R)");
        JMenuItem runItem = new JMenuItem("Run");
        JMenuItem runTokenItem = new JMenuItem("词法分析");
        runMenu.add(runItem);
        runMenu.add(runTokenItem);
        menuBar.add(runMenu);
        //运行按钮监听
        runItem.addActionListener(e->{
            textAreaLexer.setText("");
            textAreaParser.setText("");
            textAreaConsole.setText("");
            String cmmString = textPane.getText();
            System.out.println(cmmString);
            if(!cmmString.equals("")){
                try{
                    Parser parser = new Parser(cmmString);
                    parser.run();
                    SemanticAnalysis analysis = new SemanticAnalysis(parser.getProgram());
                    analysis.run();
                    String result = analysis.getResult();
                    textAreaConsole.setText(result);
                } catch (IOException e1) {
                    textAreaConsole.setText("");
                    textAreaConsole.setText(e1.getMessage());
                } catch (CMMException e2) {
                    textAreaConsole.setText("");
                    int lineNo = e2.getLineNum();
                    String error = e2.getMessage()+" "+" Line No: "+lineNo;
                    textAreaConsole.setText(error);
                }
            }else {
                JOptionPane.showMessageDialog(frmCmm, "当前编辑区域为空。");
            }
        });
        JMenu helpMenu = new JMenu("Help(H)");
        //帮助按钮监听
        helpMenu.addActionListener(e -> JOptionPane.showMessageDialog(frmCmm, "请参考CMM示例文件。"));
        menuBar.add(helpMenu);
    }
}
