import java.io.*;

public class Assembler {
    public static void AssemblerMain(String source_file) {
        String fileName = source_file.substring(0, source_file.lastIndexOf("."));

        String target_file = fileName + ".hack";

        Parser p1 = new Parser(source_file);
        System.out.println("\n------ HACK File saved at: " + target_file + " ------");


        try {
            FileWriter fw = new FileWriter(target_file);
            fw.write(p1.translate());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
