package lexer;

import exceptions.CMMException;
import parser.Parser;
import semantic.SemanticAnalysis;

import java.io.IOException;

/**
 * Created by 79300 on 2017/10/8.
 */
public class Main {

    public static void main(String[] args) throws IOException, CMMException {
        if(args.length<1){
            System.err.println("Please input filepath");
            System.exit(1);
        }
        String test=FileIO.read(args[0]);
        Parser parser = new Parser(test);
        parser.run();
        SemanticAnalysis analysis = new SemanticAnalysis(parser.getProgram());
        analysis.run();
    }
}
