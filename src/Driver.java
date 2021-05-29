import java.util.*;

public class Driver {
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter input folder path");
        String inFolderPath = sc.nextLine();
        sc.close();

        /* while (!source_file.substring(source_file.length() - 4, source_file.length()).equals(".asm")) {
            System.out.println("Input is not an asm file");
            source_file = sc.nextLine();
        }*/

        String translatedPath = Translator.translatorMain(inFolderPath);
        Assembler.AssemblerMain(translatedPath);
    }
}
