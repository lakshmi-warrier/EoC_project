import java.io.*;
import java.util.*;

public class VMWriter 
{
    static enum SEGMENT {CONST,ARG,LOCAL,STATIC,THIS,THAT,POINTER,TEMP,NONE};
    static enum COMMAND {ADD,SUB,NEG,EQ,GT,LT,AND,OR,NOT};

    static HashMap<SEGMENT,String> segmentStringHashMap = new HashMap<SEGMENT, String>();
    static HashMap<COMMAND,String> commandStringHashMap = new HashMap<COMMAND, String>();
    static PrintWriter printWriter;

    static 
    {
        segmentStringHashMap.put(SEGMENT.CONST,"constant");
        segmentStringHashMap.put(SEGMENT.ARG,"argument");
        segmentStringHashMap.put(SEGMENT.LOCAL,"local");
        segmentStringHashMap.put(SEGMENT.STATIC,"static");
        segmentStringHashMap.put(SEGMENT.THIS,"this");
        segmentStringHashMap.put(SEGMENT.THAT,"that");
        segmentStringHashMap.put(SEGMENT.POINTER,"pointer");
        segmentStringHashMap.put(SEGMENT.TEMP,"temp");

        commandStringHashMap.put(COMMAND.ADD,"add");
        commandStringHashMap.put(COMMAND.SUB,"sub");
        commandStringHashMap.put(COMMAND.NEG,"neg");
        commandStringHashMap.put(COMMAND.EQ,"eq");
        commandStringHashMap.put(COMMAND.GT,"gt");
        commandStringHashMap.put(COMMAND.LT,"lt");
        commandStringHashMap.put(COMMAND.AND,"and");
        commandStringHashMap.put(COMMAND.OR,"or");
        commandStringHashMap.put(COMMAND.NOT,"not");
    }
    VMWriter(File fOut) 
    {
        try 
        {
            printWriter = new PrintWriter(fOut);
        } 
        catch (FileNotFoundException e)
        {
           
        }
    }
    static void writePush(SEGMENT segment, int index)
    {
        writeCommand("push",segmentStringHashMap.get(segment),String.valueOf(index));
    }
    static void writePop(SEGMENT segment, int index)
    {
        writeCommand("pop",segmentStringHashMap.get(segment),String.valueOf(index));
    }
    static void writeArithmetic(COMMAND command)
    {
        writeCommand(commandStringHashMap.get(command),"","");
    }
    static void writeLabel(String label)
    {
        writeCommand("label",label,"");
    }
    static void writeGoto(String label)
    {
        writeCommand("goto",label,"");
    }
    static void writeIf(String label)
    {
        writeCommand("if-goto",label,"");
    }
    static void writeCall(String name, int nArgs)
    {
        writeCommand("call",name,String.valueOf(nArgs));
    }
    static void writeFunction(String name, int nLocals)
    {
        writeCommand("function",name,String.valueOf(nLocals));
    }
    static void writeReturn()
    {
        writeCommand("return","","");
    }
     static void writeCommand(String cmd, String arg1, String arg2)
    {
        printWriter.print(cmd + " " + arg1 + " " + arg2 + "\n");
    }
    static void close()
    {
        printWriter.close();
    }
}