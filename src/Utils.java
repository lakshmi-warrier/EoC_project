import java.util.*;

public class Utils {
    static String code_cleaner(String s) {
        String newString = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '/')
                break;
            else
                newString += s.charAt(i);
        }

        return newString.strip();
    }

    //incomplete - not working
    static ArrayList<String> tokenise(String line) {
        ArrayList<String> code = new ArrayList<>();
        String[] codeline = line.split(" ");
        for (String token : codeline) {
            if (token.contains("(")) {
                String temp[] = token.split("\\(");
                code.add(temp[0]);
                code.add("(");
                code.add(temp[1]);
            }
            if (token.contains(")")) {
                String temp[] = token.split("\\)");
                code.add(temp[0]);
                code.add(")");
            }

            else code.add(token);
        }
        return code;
    }

    //for testing
    public static void main(String[] args) {
        Tokenizer.JackTokenizer(" method Fraction foo ( int y ) { } ");
    }
}
