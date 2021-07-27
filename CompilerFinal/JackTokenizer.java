import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JackTokenizer 
{

    static enum TYPE {KEYWORD, SYMBOL, IDENTIFIER, INTEGERCONSTANT, STRINGCONSTANT, NONE};

    static enum KEYWORD {CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN,CHAR, VOID, VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS};

    static String currentToken;
    static TYPE currentTokenType;
    static int pointer;
    static ArrayList<String> tokens;

    static Pattern tokenPatterns;
    static String keyWordReg;
    static String symbolReg;
    static String intReg;
    static String strReg;
    static String idReg;

    static HashMap<String,KEYWORD> keyWordMap = new HashMap<String, KEYWORD>();
    static HashSet<Character> opSet = new HashSet<Character>();

    static 
    {

        keyWordMap.put("class",KEYWORD.CLASS);
        keyWordMap.put("constructor",KEYWORD.CONSTRUCTOR);
        keyWordMap.put("function",KEYWORD.FUNCTION);
        keyWordMap.put("method",KEYWORD.METHOD);
        keyWordMap.put("field",KEYWORD.FIELD);
        keyWordMap.put("static",KEYWORD.STATIC);
        keyWordMap.put("var",KEYWORD.VAR);
        keyWordMap.put("int",KEYWORD.INT);
        keyWordMap.put("char",KEYWORD.CHAR);
        keyWordMap.put("boolean",KEYWORD.BOOLEAN);
        keyWordMap.put("void",KEYWORD.VOID);
        keyWordMap.put("true",KEYWORD.TRUE);
        keyWordMap.put("false",KEYWORD.FALSE);
        keyWordMap.put("null",KEYWORD.NULL);
        keyWordMap.put("this",KEYWORD.THIS);
        keyWordMap.put("let",KEYWORD.LET);
        keyWordMap.put("do",KEYWORD.DO);
        keyWordMap.put("if",KEYWORD.IF);
        keyWordMap.put("else",KEYWORD.ELSE);
        keyWordMap.put("while",KEYWORD.WHILE);
        keyWordMap.put("return",KEYWORD.RETURN);

        opSet.add('+');
        opSet.add('-');
        opSet.add('*');
        opSet.add('/');
        opSet.add('&');
        opSet.add('|');
        opSet.add('<');
        opSet.add('>');
        opSet.add('=');
    }
    public JackTokenizer(File inFile) 
    {
        try 
        {
            Scanner scanner = new Scanner(inFile);
            String preprocessed = "";
            String line = "";

            while(scanner.hasNext())
            {
                line = Comments(scanner.nextLine()).trim();

                if (line.length() > 0) 
                {
                    preprocessed += line + "\n";
                }
            }
            preprocessed = BlockComments(preprocessed).trim();

            Check();

            Matcher m = tokenPatterns.matcher(preprocessed);
            tokens = new ArrayList<String>();
            pointer = 0;

            while (m.find())
            {
                tokens.add(m.group());
            }
        } 
        catch (FileNotFoundException e) 
        {

        }
        currentToken = "";
        currentTokenType = TYPE.NONE;
    }
    static void Check()
    {
        keyWordReg = "";
        for (String seg: keyWordMap.keySet())
        {
            keyWordReg += seg + "|";
        }

        symbolReg = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
        intReg = "[0-9]+";
        strReg = "\"[^\"\n]*\"";
        idReg = "[a-zA-Z_]\\w*";

        tokenPatterns = Pattern.compile(idReg + "|" + keyWordReg + symbolReg + "|" + intReg + "|" + strReg);
    }
    static boolean HasMoreTokens() 
    {
        return pointer < tokens.size();
    }
    static void Advance()
    {
        if (HasMoreTokens()) 
        {
            currentToken = tokens.get(pointer);
            pointer++;
        }
        else 
        {
            System.out.println(" HAS NO TOKENS");
        }
        if (currentToken.matches(keyWordReg))
        {
            currentTokenType = TYPE.KEYWORD;
        }
        else if (currentToken.matches(symbolReg))
        {
            currentTokenType = TYPE.SYMBOL;
        }
        else if (currentToken.matches(intReg))
        {
            currentTokenType = TYPE.INTEGERCONSTANT;
        }
        else if (currentToken.matches(strReg))
        {
            currentTokenType = TYPE.STRINGCONSTANT;
        }
        else if (currentToken.matches(idReg))
        {
            currentTokenType = TYPE.IDENTIFIER;
        }
        else 
        {
            throw new IllegalArgumentException("Unknown token:" + currentToken);
        }
    }
    static KEYWORD KeyWords()
    {
        if (currentTokenType == TYPE.KEYWORD)
        {
            return keyWordMap.get(currentToken);
        }
        else 
        {
            throw new IllegalStateException("Current token is not a keyword!");
        }
    }
    static char Symbol()
    {
        if (currentTokenType == TYPE.SYMBOL)
        {
            return currentToken.charAt(0);
        }
        else
        {
            throw new IllegalStateException("Current token is not a symbol!");
        }
    }
    static String Identifier()
    {
        if (currentTokenType == TYPE.IDENTIFIER)
        {
            return currentToken;
        }
        else 
        {
            throw new IllegalStateException("Current token is not an identifier! current type:" + currentTokenType);
        }
    }
    static int IntegerValue()
    {
        if(currentTokenType == TYPE.INTEGERCONSTANT)
        {
            return Integer.parseInt(currentToken);
        }
        else 
        {
            throw new IllegalStateException("Current token is not an integer constant!");
        }
    }
    static String StringValue()
    {

        if (currentTokenType == TYPE.STRINGCONSTANT)
        {
            return currentToken.substring(1, currentToken.length() - 1);
        }
        else 
        {
            throw new IllegalStateException("Current token is not a string constant!");
        }
    }
    static void Pointer()
    {
        if (pointer > 0) 
        {
            pointer--;
            currentToken = tokens.get(pointer);
        }
    }
    static boolean IsOperation()
    {
        return opSet.contains(Symbol());
    }
    static String Comments(String strIn)
    {
        int position = strIn.indexOf("//");
        if (position != -1)
        {
            strIn = strIn.substring(0, position);
        }
        return strIn;
    }
    static String Space(String strIn)
    {
        String result = "";

        if (strIn.length() != 0)
        {
            String[] segs = strIn.split(" ");

            for (String s: segs)
            {
                result += s;
            }
        }
        return result;
    }
    static String BlockComments(String strIn)
    {
        int startIndex = strIn.indexOf("/*");
        if (startIndex == -1) 
        {
            return strIn;
        }
        String result = strIn;
        int endIndex = strIn.indexOf("*/");
        while(startIndex != -1)
        {
            if (endIndex == -1)
            {
                return strIn.substring(0,startIndex - 1);
            }
            result = result.substring(0,startIndex) + result.substring(endIndex + 2);
            startIndex = result.indexOf("/*");
            endIndex = result.indexOf("*/");
        }
        return result;
    }
    static TYPE token() 
    {
        return currentTokenType;
    }
    static String CurrentToken() 
    {
        return currentToken;
    }
}