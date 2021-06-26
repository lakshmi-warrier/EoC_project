
import java.util.ArrayList;
public class ASM_Codes
{
	 static int eqCount = 0;
	 static int gtCount = 0;
	 static int ltCount = 0;
	 static int callcount=0;
	
	 static String function(String fn, int var)
    {
		StringBuffer sb = new StringBuffer();
		ArrayList<String> AL = new ArrayList<>();
		
		AL.add("// function " + fn + " " + var);
		AL.add("(" + fn + ")");
		for (int i = 0; i < var; i++)
		{
			AL.add("@SP");
			AL.add("A=M");
			AL.add("M=0");
			AL.add("@SP");
			AL.add("M=M+1");
		}
		for (String s : AL ) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
    }

	static String Call(String vmName,String fn, int arg)
	{
		ArrayList<String> AL = new ArrayList<>();
		// add comment
		AL.add("// call " + fn + " " + arg);
		String returnAddress;
		returnAddress = vmName + "-return-address-" + (callcount);
		callcount++;
	
		// 1. push return address
		AL.add("@" + returnAddress);
		AL.add("D=A");
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
		AL.add("@SP");
		AL.add("M=M+1");
	
		// 2. push LCL
		AL.add("@LCL");
		AL.add("D=M");
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
		AL.add("@SP");
		AL.add("M=M+1");
	
		// 3. push ARG
		AL.add("@ARG");
		AL.add("D=M");
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
		AL.add("@SP");
		AL.add("M=M+1");
	
		// 4. push THIS
		AL.add("@THIS");
		AL.add("D=M");
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
		AL.add("@SP");
		AL.add("M=M+1");
	
		// 5. push THAT
		AL.add("@THAT");
		AL.add("D=M");
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
		AL.add("@SP");
		AL.add("M=M+1");
	
		// 6. calculate ARG for called function
		AL.add("@5");
		AL.add("D=A");
		AL.add("@" + arg);
		AL.add("D=D+A");
		AL.add("@SP");
		AL.add("D=M-D");
		AL.add("@ARG");
		AL.add("M=D");
	
		// 7. calculate LCL for called function
		AL.add("@SP");
		AL.add("D=M");
		AL.add("@LCL");
		AL.add("M=D");
	
		// 8. go to function
		AL.add("@" + fn);
		AL.add("0;JMP");
	
		// 9. mark return address
		AL.add("(" + returnAddress + ")");
		StringBuffer sb = new StringBuffer();
	    for (String s : AL ) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String Return()
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		// add comment
		AL.add("// return");
	
		//  R13 <--- FRAME = LCL
		AL.add("@LCL");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	
		// R14 <--- RET = *(FRAME - 5)
		AL.add("@R13");
		AL.add("D=M");
		AL.add("@5");
		AL.add("A=D-A");
		AL.add("D=M");
		AL.add("@R14");
		AL.add("M=D");
	
		//  *ARG = pop()
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@ARG");
		AL.add("A=M");
		AL.add("M=D");
	
		//  SP = ARG + 1
		AL.add("@ARG");
		AL.add("D=M+1");
		AL.add("@SP");
		AL.add("M=D");
	
		// THAT = *(FRAME - 1)
		AL.add("@R13");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@THAT");
		AL.add("M=D");
	
		// THIS = *(FRAME - 2)
		AL.add("@R13");
		AL.add("D=M");
		AL.add("@2");
		AL.add("A=D-A");
		AL.add("D=M");
		AL.add("@THIS");
		AL.add("M=D");
	
		// ARG = *(FRAME - 3)
		AL.add("@R13");
		AL.add("D=M");
		AL.add("@3");
		AL.add("A=D-A");
		AL.add("D=M");
		AL.add("@ARG");
		AL.add("M=D");
	
		//  LCL = *(FRAME - 4)
		AL.add("@R13");
		AL.add("D=M");
		AL.add("@4");
		AL.add("A=D-A");
		AL.add("D=M");
		AL.add("@LCL");
		AL.add("M=D");
	
		// goto RET
		AL.add("@R14");
		AL.add("A=M");
		AL.add("0;JMP");
		
		for (String s : AL ) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	static String Goto(String line)
	{
	    ArrayList<String> AL = new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
		AL .add("// goto " + line);
		AL .add("@" + line);
		AL .add("0;JMP");
		
	    for (String s : AL ) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
		
	}
	static String IfGoto(String line)
	{
	    ArrayList<String> AL  = new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
		AL .add("// if-goto " + line);
	
		// pop the top most value
		AL .add("@SP");
		AL .add("AM=M-1");
		// check condition and jump
		AL .add("D=M");
		AL .add("@" + line);
		AL .add("D;JNE");
	    
	    for (String s : AL ) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String Label(String line)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	    AL.add("// label " + line);
		AL.add("(" + line + ")");
		
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String pushconstant(String line,int n)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    AL.add("// push constant "+n);
	    // Load (segment + n) content
		AL.add("@" + n);
		AL.add("D=A");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
	    
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	static String PushArg(String line,int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// push argument " + n);
	
		// Load (segment + n) content
		AL.add("@ARG");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("A=D+A");
		AL.add("D=M");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
		
		for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PushLocal(String line,int n) 
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// push local " + n);
	
		// Load (segment + n) content
		AL.add("@LCL");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("A=D+A");
		AL.add("D=M");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
	
		
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	static String PushThis(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// push this " + n);
	
		// Load (segment + n) content
		AL.add("@THIS");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("A=D+A");
		AL.add("D=M");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
	
		for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PushThat(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// push that " + n);
	
		// Load (segment + n) content
		AL.add("@THAT");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("A=D+A");
		AL.add("D=M");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
	
		for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PushStatic(String vmName,String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
	
		// Add comment to al
		AL.add("// push static " + n);
	
		// Load (segment + n) content
		AL.add("@" + vmName + ".static." + n);
		AL.add("D=M");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
	
		StringBuffer sb = new StringBuffer();
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PushPointer(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		// Add comment to al
		AL.add("// push pointer " + n);
	
		// Load (segment + n) content
		AL.add("@3");
		AL.add("D=A");
		AL.add("@" + n);
		AL.add("A=D+A");
		AL.add("D=M");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
	
		for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PushTemp(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// push temp " + n);
	
		// Load (segment + n) content
		AL.add("@5");
		AL.add("D=A");
		AL.add("@" + n);
		AL.add("A=D+A");
		AL.add("D=M");
	
		// Push to stack
		AL.add("@SP");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M+1");
	
		for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PopArgument(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		// Add comment to al
		AL.add("// pop argument " + n);
	
		AL.add("@ARG");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("D=D+A");
		AL.add("@R13");
		AL.add("M=D");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PopLocal(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// pop local " + n);
	
		AL.add("@LCL");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("D=D+A");
		AL.add("@R13");
		AL.add("M=D");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	
	}
	static String PopThis(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// pop this " + n);
	
		AL.add("@THIS");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("D=D+A");
		AL.add("@R13");
		AL.add("M=D");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PopThat(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// pop that " + n);
	
		AL.add("@THAT");
		AL.add("D=M");
		AL.add("@" + n);
		AL.add("D=D+A");
		AL.add("@R13");
		AL.add("M=D");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PopStatic(String vmName,String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// pop static " + n);
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	
		AL.add("@" + vmName + ".static." + n);
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PopPointer(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// pop pointer " + n);
	
		AL.add("@3");
		AL.add("D=A");
		AL.add("@" + n);
		AL.add("D=D+A");
		AL.add("@R13");
		AL.add("M=D");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("A=M");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String PopTemp(String line, int n)
	{
		ArrayList<String> AL = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
	
		// Add comment to al
		AL.add("// pop temp " + n);
	
		AL.add("@5");
		AL.add("D=A");
		AL.add("@" + n);
		AL.add("D=D+A");
		AL.add("@R13");
		AL.add("M=D");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("A=M");
		AL.add("M=D");
	
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		 for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
		
	static String eq(String vmName,String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    String labelEQTrue = vmName + ".EQ.true." + eqCount;
		String labelEQEnd = vmName + ".EQ.end." + eqCount;
	    eqCount++;
	
		// Add comment to al
		AL.add("// eq");
	
		// Load second operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		// Load first operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	
		// Sub
		AL.add("@R13");
		AL.add("D=D-M");
	
		// Check to jump
		AL.add("@" + labelEQTrue);
		AL.add("D;JEQ");
	
		// No jump
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=0");
		AL.add("@" + labelEQEnd);
		AL.add("0;JMP");
	
		// Jump to here if true/equal
		AL.add("(" + labelEQTrue + ")");
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=-1");
	
		// End
		AL.add("(" + labelEQEnd + ")");
	   
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	 
	static String lt(String vmName,String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    
	    String labelLTTrue = vmName + ".LT.true." + ltCount;
		String labelLTEnd = vmName + ".LT.end." + ltCount;
		ltCount++;
	
		// Add comment to al
		AL.add("// lt");
	
		// Load second operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		// Load first operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	
		// Sub
		AL.add("@R13");
		AL.add("D=D-M");
	
		// Check to jump
		AL.add("@" + labelLTTrue);
		AL.add("D;JLT");
	
		// No jump
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=0");
		AL.add("@" + labelLTEnd);
		AL.add("0;JMP");
	
		// Jump to here if true/lesser than
		AL.add("(" + labelLTTrue + ")");
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=-1");
	
		// End
		AL.add("(" + labelLTEnd + ")");
	
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String gt(String vmName,String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    String labelGTTrue = vmName + ".GT.true." + gtCount;
		String labelGTEnd = vmName + ".GT.end." + gtCount;
		gtCount++;
	
		// Add comment to al
		AL.add("// gt");
	
		// Load second operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		// Load first operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	
		// Sub
		AL.add("@R13");
		AL.add("D=D-M");
	
		// Check to jump
		AL.add("@" + labelGTTrue);
		AL.add("D;JGT");
	
		// No jump
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=0");
		AL.add("@" + labelGTEnd);
		AL.add("0;JMP");
	
		// Jump to here if true/greater than
		AL.add("(" + labelGTTrue + ")");
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=-1");
	
		// End
		AL.add("(" + labelGTEnd + ")");
	
	   
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String add(String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    AL.add("// add");
	
		// Load second operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		// Load first operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	
		// Add
		AL.add("@R13");
		AL.add("D=D+M");
	
		// Store the result
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=D");
	    
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	
	static String sub(String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    AL.add("// sub");
	
		// Load second operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	    
		// Update stack pointer
		AL.add("@SP");
	    
		AL.add("M=M-1");
	    
		// Load first operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	    
		// Sub
		AL.add("@R13");
		AL.add("D=D-M");
	
		// Store the result
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=D");
	   
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	static String neg(String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    AL.add("// neg");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=-M");
	    
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	static String and(String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();

		// Add comment to al
		AL.add("// and");
	
		// Load second operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		// Load first operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	
		// And
		AL.add("@R13");
		AL.add("D=D&M");
	
		// Store the result
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=D");
	
	    	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	static String or(String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    // Add comment to al
		AL.add("// or");
	
		// Load second operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
		AL.add("@R13");
		AL.add("M=D");
	
		// Update stack pointer
		AL.add("@SP");
		AL.add("M=M-1");
	
		// Load first operand
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("D=M");
	
		// Or
		AL.add("@R13");
		AL.add("D=D|M");
	
		// Store the result
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=D");
	    
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
	static String not(String line)
	{
	    ArrayList <String> AL =new ArrayList<>();
	    StringBuffer sb = new StringBuffer();
	    AL.add("// not");
	
		AL.add("@SP");
		AL.add("A=M-1");
		AL.add("M=!M");
	    
	    for (String s : AL) 
	    {
	     sb.append(s);
	     sb.append("\n");
	    }
	    String al = sb.toString();
		return al;
	}
}
