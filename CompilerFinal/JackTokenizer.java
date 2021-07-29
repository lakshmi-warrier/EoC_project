import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JackTokenizer 
{
    static enum TYPE 
    {
        KEYWORD, SYMBOL, IDENTIFIER, INTEGERCONSTANT, STRINGCONSTANT, NONE
    };

    static enum KEYWORD 
    {
        CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN,CHAR, VOID, VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
    };

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

    //static block initialises the class variables
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

            //white space and comments are being handled
            while(scanner.hasNext())
            {
                line = Comments(scanner.nextLine()).trim();

                //considering a line only if the line is not null
                if (line.length() > 0) 
                {
                    preprocessed += line + "\n";
                }
            }

            //handling block comments "/* */"
            preprocessed = BlockComments(preprocessed).trim();

            //initialise the regex for tokenPatterns so that later, only tokens are added to the arraylist of tokens
            Check();

            Matcher m = tokenPatterns.matcher(preprocessed);
            tokens = new ArrayList<String>();
            pointer = 0;

            while (m.find())
            {
                //add matching groups (string) to the arraylist
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

        // to set the keywords like static|void|method|var|constructor|false|this|do|while|int|boolean|field|null|else|function|char|true|let|class|if|return|
        for (String seg: keyWordMap.keySet())
        {
            keyWordReg += seg + "|";
        }

        //a token should be one of symbol, integer, string or identifier
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
        // checks if the the token type matches with the keyword, symbol, integer constant, string constant or identifier
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
        //checks if the current token is keyword or not
        // if it is a keyword, the corresponding keyword will be returned
        // else a error will be printed
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
        //checks the current token is a symbol or not
        //if it is a symbol, the symbol will be returned
        //else error will be printed
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
        //checks the current token is an identifier or not
        //if it is an identifier, the current token will be returned
        //else error will be printed
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
        //checks the current token is an integer value or not
        //if it is an integer, the integer constant will be returned
        //else error will be printed
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
        //checks the current token is a string or not
        //if it is a string, the current token will be returned
        //else error will be printed
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
        //sets currentToken - the element that's at pointer
        if (pointer > 0) 
        {
            pointer--;
            currentToken = tokens.get(pointer);
        }
    }
    static boolean IsOperation()
    {
        // checks the hashmap opset contains the symbol 
        return opSet.contains(Symbol());
    }
    static String Comments(String strIn)
    {
        //form comment handling
        int position = strIn.indexOf("//");
        if (position != -1)
        {
            strIn = strIn.substring(0, position);
        }
        return strIn;
    }
    static String Space(String strIn)
    {
        //for white space
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
        //handling block comments
        int startIndex = strIn.indexOf("/*");
        if (startIndex == -1) 
        {
            return strIn;
        }
        String result = strIn;
        int endIndex = strIn.indexOf("*/");

        //loop runs till there are no /* and */ left
        while(startIndex != -1)
        {
            if (endIndex == -1)
            {
                return strIn.substring(0,startIndex - 1);
            }

            //avoiding the contents in the block
            result = result.substring(0,startIndex) + result.substring(endIndex + 2);
            startIndex = result.indexOf("/*");
            endIndex = result.indexOf("*/");
        }
        return result;
    }
    static TYPE token() 
    {
        // to return the current tokentype
        return currentTokenType;
    }
    static String CurrentToken() 
    {
        //to return currenttoken
        return currentToken;
    }
}