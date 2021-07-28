import java.io.*;
import java.util.*;
public class JackCompiler
{
    public static void main(String[] args) 
    {
        System.out.print("Enter the path of the folder: ");
        Scanner in = new Scanner(System.in);
        String fileInName = in.next(); // folder path is taken as a string
        File fileIn = new File(fileInName);

        String fileOutPath = "";

        File fileOut;

        ArrayList<File> jackFiles = new ArrayList<File>(); // adding the file name to arraylist

        if (fileIn.isFile()) // If a file path is entered
        {
            String path = fileIn.getAbsolutePath();
            if (!path.endsWith(".jack")) // checks if the path entered ends with .jack
            {
                System.out.println("JACK FILE NOT PRESENT");
            }
            jackFiles.add(fileIn); // if the path ends with .jack it gets added onto the array list
        } 
        else if (fileIn.isDirectory()) //if it is a directory get all jack files under this directory
        {
            jackFiles = getJackFiles(fileIn); // goes to the method getJackFiles

            if (jackFiles.size() == 0) //if no  file in this directory
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
            // creates a vm file where the folder where the .jack is being stored
            fileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".vm"; 
            fileOut = new File(fileOutPath);

            CompilationEngine compilationEngine = new CompilationEngine(f,fileOut);
            compilationEngine.ClassCompiler(); // goes to the method ClassCompiler in the class compilation Engine

            System.out.println(" File is created in : " + fileOutPath); // file path where the vm file generated is being printed
        }
    }
    // to get the jack files from the folder
    public static ArrayList<File> getJackFiles(File dir)
    {
        File[] files = dir.listFiles();
        ArrayList<File> result = new ArrayList<File>(); // an array list is created to add jack files
        if (files == null) // checks if the folder is null
        {
            return result; 
        }
        for (File f:files)
        {
            if (f.getName().endsWith(".jack"))
            {
                result.add(f); // if the files in the folder ends with .jack, it gets added the array list
            }
        }
        return result;
    }
}
