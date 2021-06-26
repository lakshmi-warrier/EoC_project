public class CodeCleaning
{
    static String NewLine="";
    static String Cleaning(String line)
    {
       NewLine= CommentHandling(line);
       NewLine= WhileSpaceHandling(line);
       return NewLine;
    }
    static String CommentHandling(String line)
    {
        if(hasComments(line))
        {
            NewLine=RemoveComments(line);
        }
        return NewLine;
    }
    static String WhileSpaceHandling(String line)
    {
        for(int i=0;i<line.length();i++)
        {
            if(line.charAt(i) != ' ')
            {
                NewLine+=line.charAt(i);
            }
        }
        NewLine=NewLine.trim();
        return NewLine;
    }
    static boolean hasComments(String line)
    {
        boolean comment= false;
        if(line.contains("//")||line.contains("/*")||line.startsWith("*"))
        {
            comment=true;
        }
        return comment;
    }
    static String RemoveComments(String line)
    {
        int length;
        if(hasComments(line))
        {
            if(line.startsWith("*"))
            {
                length=line.indexOf("*");
            }
            else if (line.contains("/*"))
            {
                length = line.indexOf("/*");
            }
            else
            {
                length = line.indexOf("//");
            }
            NewLine = line.substring(0, length).trim();
        }
        return NewLine;
    }    
}
