import java.util.*;
public class VM_Translate 
{
	static String[] codes ;
	static String St="";
	static int n;
	static String vmName;
    static String Fcode="";
   
	
	static String BootStrap()//sys
	{
		ArrayList<String> AL = new ArrayList<>();
		AL.add("// Bootstrap code");
		AL.add("@256");
		AL.add("D=A");
		AL.add("@SP");
		AL.add("M=D");
		AL.add(Sys("Sys.init", 0));
		StringBuffer sb = new StringBuffer();
        
		for (String s : AL ) 
        {
         sb.append(s);
         sb.append("\n");
        }
        String al = sb.toString();
		return al;
	}
    
	static String Sys(String function, int nArgs)
	{
		List<String> AL = new ArrayList<>();
		// add comment
		AL.add("// call " + function + " " + nArgs);

		String returnAddress = "after-finishing-all-tasks";

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
		AL.add("@" + nArgs);
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
		AL.add("@" + function);
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
	
	static String Parse(String line)//splitting wrt space
    {
		String words=" ";
        codes = line.split(words);
		if(codes.length==1)
		{
			Fcode=One();
		}
		else if(codes.length==2)
		{
			Fcode=Two();
		}
		else
		{
			Fcode=Three();
		}
       return Fcode;
    }
	
	static String Three()
	{
		if(codes.length==3)
		{
			String code="";
			if("call".equals(codes[0]))
			{
				String fn=codes[1];
				String arg = codes[2];
				n=Integer.parseInt(arg);
				Fcode=ASM_Codes.Call(vmName,fn,n);
			}
			
			if("function".equals(codes[0]))
			{
				String fn=codes[1];
				String arg = codes[2];
				n=Integer.parseInt(arg);
				Fcode=ASM_Codes.function(fn,n);
			}
			if("push".equals(codes[0]))
			{
				if ("argument".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PushArg(code,n);
				}
				if ("local".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PushLocal(code,n);
				}
				if ("this".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PushThis(code,n);
				}
				if ("that".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PushThat(code,n);
				}
				if ("static".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PushStatic(vmName,code,n);
				}
				if ("constant".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.pushconstant(code,n);
				}
				if ("pointer".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PushPointer(code,n);
				}
				if ("temp".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PushTemp(code,n);
				}
			}
			if("pop".equals(codes[0]))
			{
				if ("argument".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PopArgument(code,n);
				}
				if ("local".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PopLocal(code,n);
				}
				if ("this".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PopThis(code,n);
				}
				if ("that".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PopThat(code,n);
				}
				if ("static".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PopStatic(vmName,code,n);
				}
				if ("pointer".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PopPointer(code,n);
				}
				if ("temp".equals(codes[1]))
				{
					code=codes[1];
					St=codes[2];
					n=Integer.parseInt(St);
					Fcode=ASM_Codes.PopTemp(code,n);
				}
			}
		}
		return Fcode;
	}
	

	static String Two()
	{
		if("label".equals(codes[0]))
		{
			Fcode=ASM_Codes.Label(codes[1]);
		}
		if("goto".equals(codes[0]))
		{
			Fcode=ASM_Codes.Goto(codes[1]);
		}
		if("if-goto".equals(codes[0]))
		{
			Fcode=ASM_Codes.IfGoto(codes[1]);	
		}
		return Fcode;
	}

	static String One()
	{
		if("return".equals(codes[0]))
		{
			Fcode=ASM_Codes.Return();
		}
		if("eq".equals(codes[0]))
		{
			Fcode= ASM_Codes.eq(vmName,codes[0]);
		}
		if("lt".equals(codes[0]))
		{
			Fcode=ASM_Codes.lt(vmName,codes[0]);
		}
		if("gt".equals(codes[0]))
		{
			Fcode=ASM_Codes.gt(vmName,codes[0]);
		}    
		if("add".equals(codes[0]))
		{
			Fcode=ASM_Codes.add(codes[0]);
		}
		if("sub".equals(codes[0]))
		{
			Fcode=ASM_Codes.sub(codes[0]);
		}    
		if("neg".equals(codes[0]))
		{
			Fcode=ASM_Codes.neg(codes[0]);
		}
		if("and".equals(codes[0]))
		{
			Fcode= ASM_Codes.and(codes[0]);
		}
		if("or".equals(codes[0]))
		{
			Fcode=ASM_Codes.or(codes[0]);
		}
		if("not".equals(codes[0]))
		{
			Fcode=ASM_Codes.not(codes[0]);
		}

		return Fcode;
	}
}
