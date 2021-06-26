public class JackParsing 
{
    static String Symbols = "{}()[].,;+-*/&|<>=-~";
    static String Operations = "+-*/&|<>=";
    static String statements(String token) // for class, method, int
    {
        /* need to print in the format 
            <keyword> statements </keyword>
        */
        String Statement = "<keyword> "+token+" </keyword>";
        return Statement;
    }
    static String Symbols(String token)
    {
        if(isSymbol(token))
        {
            String Symbol = "<symbol> "+token+" </symbol>";
            return Symbol;
        }
        else
        {
            return "not a symbol";
        }
    }
    static String Identifiers(String token)
    {
        String Identifiers = "<identifier> "+token+" </identifier>";
        return Identifiers;  
    }
    static String IntegerConstant(String token)
    {
        if(isInteger(token))
        {
            String IntegerConstant = "<integerConstant> "+token+" </integerConstant>";
            return IntegerConstant;
        }
        else
        {
            return " not a integer";
        }
    }
    static String stringConstant(String token)
    {
        String StringConst = "<stringConstant> "+token+" </stringConstant>";
        return StringConst;
    }
    static boolean isInteger(String token)
    {
        int tokens=0;
        if(tokens==Integer.parseInt(token))
        {
            System.out.println("integer present");
            return true;
        }
        else
        {
            System.out.println("integer not present");
            return false;
        }
    }
    static boolean isSymbol(String token)
    {
        if(token==Symbols)
        {
            System.out.println("symbols present");
            return true;
        }
        else
        {
            System.out.println("symbols not present");
            return false;
        }
    }
}
