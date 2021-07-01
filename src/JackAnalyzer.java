
//input: filename.jack or directoryName
//output: filename.xml or filename.xml for every file n the directory
import java.util.*;
import java.io.*;

class JackAnalyzer {
    public static void jackAnalyser(String inFolderPath) {

        ArrayList<File> JackFiles = new ArrayList<>();// collection of all the jack files
        File in = new File(inFolderPath);
        if (in.isDirectory()) {
            File[] files = in.listFiles();
            for (File file : files) {
                String path = file.getAbsolutePath();
                try {
                    String extension = path.substring(path.lastIndexOf("."));
                    if (extension.equals(".jack"))
                        JackFiles.add(file);
                } catch (StringIndexOutOfBoundsException e) {
                    // if the folder contains no files, index out of bound exception is thrown as
                    // '.' is missing
                    System.out.println("Can't find any JACK files in the path. Please check the path\nClosing program");
                    System.exit(0);
                }

                String finalCode = "";
                for (File jFile : JackFiles) {
                    String outFileName = jFile.getName();
                    String outFilePath = inFolderPath + "\\" + outFileName.substring(0, outFileName.length() - 5)
                            + ".xml";

                    try {
                        Scanner read_File = new Scanner(jFile);
                        finalCode = Tokenizer.JackTokenizer(read_File.nextLine());
                        System.out.println(finalCode);
                    } catch (FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                
                    System.out.println(outFilePath);
                    File output = new File(outFilePath);
                    FileWriter myWriter;
                    try {
                        myWriter = new FileWriter(output);

                        //String finalCode = Tokenizer.JackTokenizer("method Fraction foo ( int y ) { } ");
                        myWriter.write(finalCode);
                        myWriter.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Not a directory, abort");
            System.exit(0);
        }
    }
}