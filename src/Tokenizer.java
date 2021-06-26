import java.util.*;
 public class Tokenizer
{
    static String[] CodeLine ;
    static String Keywords()
    {
        List<String> keywords = new ArrayList<>();
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

        // to convert the array list to String
		StringBuffer sb = new StringBuffer();
        for (String s : keywords ) 
        {
         sb.append(s);
         sb.append("\n");
        }
        String keyW = sb.toString();
		return keyW;
    }
    static void JackTokenizer(String token)
    {
        CodeCleaning.Cleaning(token); // for comment and whitespace handling
        CodeLine = token.split(" "); // split into array lists

        /* for keywords: 
            while statement
            if statement
            let statement
        */
        for(int i=0; i<=CodeLine.length;i++)
        {
            System.out.println("codeline length: "+CodeLine.length);
            if(CodeLine[i].contains(Keywords()))
            {
                JackParsing.statements(token);
            }
            else if(!CodeLine[i].contains(JackParsing.Symbols))
            {
                JackParsing.Symbols(token);
            }

            
        }
    }

}