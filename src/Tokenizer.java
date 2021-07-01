import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    // static ArrayList<String> CodeLine;

    static String[] CodeLine;
    static List<String> keywords;
    static List<String> numbers;
    static int no;

    static List<String> Keywords() {
        keywords = new ArrayList<>();
        keywords.add("class");
        keywords.add("constructor");
        keywords.add("function");
        keywords.add("method");
        keywords.add("field");
        keywords.add("static");
        keywords.add("var");
        keywords.add("int");
        keywords.add("char");
        keywords.add("boolean");
        keywords.add("void");
        keywords.add("true");
        keywords.add("false");
        keywords.add("null");
        keywords.add("this");
        keywords.add("do");
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("return");
        keywords.add("let");
        return keywords;
    }

    static void JackTokenizer(String token) {
        token = Utils.code_cleaner(token); // for comment and whitespace handling
        // CodeLine =Utils.tokenise(token); // split into array lists

        CodeLine = token.split(" ");
        for (String line : CodeLine) {
            //System.out.println("Line 41: " + line);
            if (isNumeric(line)) {
                token = IntegerConstant(line);
                System.out.println(token);
            } else if (isSymbol(line)) {
                token = Symbols(line);
                System.out.println(token);
            } else if (isStringConstant(line)) {
                token = stringConstant(line.substring(1, line.length() - 1));
                System.out.println(token);
            } else if (isKeyword(line)) {
                token = statements(line);
                System.out.println(token);
            } else {
                token = Identifiers(line);
                System.out.println(token);
            }
        }
    }

    static String statements(String token) // for class, method, int
    {
        /*
         * need to print in the format <keyword> statements </keyword>
         */
        String Statement = "<keyword> " + token + " </keyword>";
        return Statement;
    }

    static String Symbols(String token) {
        String Symbol = "<symbol> " + token + " </symbol>";
        return Symbol;
    }

    static String Identifiers(String token) {
        String Identifiers = "<identifier> " + token + " </identifier>";
        return Identifiers;
    }

    static String IntegerConstant(String token) {
        String IntegerConstant = "<integerConstant> " + token + " </integerConstant>";
        return IntegerConstant;
    }

    static String stringConstant(String token) {
        String StringConst = "<stringConstant> " + token + " </stringConstant>";
        return StringConst;
    }

    static boolean isSymbol(String token) {

        Pattern P = Pattern.compile("[&*();,+=|.<>{}\\[\\]~-]");
        Matcher m = P.matcher(token);
        boolean found = m.find();
        if (found) {
            return true;
        } else {
            return false;
        }
    }

    static boolean isStringConstant(String token) {

        if (token.startsWith("\"")) {
            return true;
        } else {
            return false;
        }
    }

    static boolean isKeyword(String token) {
        if (Keywords().contains(token)) {
            return true;
        } else {
            return false;
        }
    }

    static boolean isNumeric(String line) {
        try {
            Integer.parseInt(line);
            return true;
        } catch (NumberFormatException e) {

        }
        return false;
    }
}