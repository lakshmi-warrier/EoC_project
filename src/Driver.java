import java.util.*;

public class Driver {
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter input folder path");
        String inFolderPath = sc.nextLine();
        sc.close();

        JackAnalyzer.jackAnalyser(inFolderPath);
        String translatedPath = Translator.translatorMain(inFolderPath);
        Assembler.AssemblerMain(translatedPath);
    }
}
