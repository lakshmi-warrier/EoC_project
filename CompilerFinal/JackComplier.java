import java.io.*;
import java.util.*;
public class JackComplier 
{
    public static ArrayList<File> getJackFiles(File dir)
    {
        File[] files = dir.listFiles();
        ArrayList<File> result = new ArrayList<File>();
        if (files == null) return result;

        for (File f:files)
        {
            if (f.getName().endsWith(".jack"))
            {
                result.add(f);
            }
        }
        return result;
    }
    public static void main(String[] args) 
    {
        System.out.print("Enter file name: ");
        Scanner in = new Scanner(System.in);
        String fileInName = in.next();
        File fileIn = new File(fileInName);

        String fileOutPath = "";

        File fileOut;

        ArrayList<File> jackFiles = new ArrayList<File>();

        if (fileIn.isFile()) 
        {
            //if it is a single file, see whether it is a vm file
            String path = fileIn.getAbsolutePath();
            if (!path.endsWith(".jack")) 
            {
                System.out.println("JACK FILE NOT PRESENT");
            }
            jackFiles.add(fileIn);
        } 
        else if (fileIn.isDirectory()) 
        {
            //if it is a directory get all jack files under this directory
            jackFiles = getJackFiles(fileIn);

            //if no  file in this directory
            if (jackFiles.size() == 0) 
            {
                System.out.println(" NO JACK FILE FOUND");
            }
        }
        else
        {
            System.out.println(" !!!!! NO SUCH FILE !!!!!");
        }
        for (File f: jackFiles) 
        {
            fileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".vm";
            fileOut = new File(fileOutPath);

            CompilationEngine compilationEngine = new CompilationEngine(f,fileOut);
            compilationEngine.ClassCompiler();

            System.out.println(" File is created in : " + fileOutPath);
        }
    }
}
