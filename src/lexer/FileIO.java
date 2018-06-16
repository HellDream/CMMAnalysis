package lexer;

import java.io.*;

/**
 * Created by 79300 on 2017/10/8.
 */
public class FileIO {
    public static String read(String filename){
        File file = new File(filename);
        StringBuilder stringBuilder=new StringBuilder();
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String s;
            while((s=bufferedReader.readLine())!=null){
                stringBuilder.append(s).append('\n');
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file: "+filename);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Read file error!");
            System.exit(1);
        }
        return stringBuilder.toString();
    }
}
