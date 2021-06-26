import java.util.*;
import java.io.*;
public class Main_VM 
{
	 static String CurrentFile="";
	 
	 public static void main(String[] files) 
	 {
		try 
		{
			 Scanner sc=new Scanner(System.in);
			 System.out.println("Enter the directory location");
			 String Dpath=sc.nextLine();
			 System.out.println("Enter path of the asm file");
			 String Wpath=sc.nextLine();
			 sc.close();
			    
			 File DP = new File(Dpath);
			 System.out.println(DP.getName());//returns name of file object
			 File FL[] = DP.listFiles(); //returns path names indicated in the directory 
				 
			
			 ArrayList<File> VMFile=new ArrayList<File>();
			 int vmcount=0;
				 
			 System.out.println("List of files and directories in the specified directory:");
			 for(File file : FL) 
			 {
				String F=file.getName();
				String E=F.substring(F.lastIndexOf('.')+1); // returns position of the last occurrence of specified character
			                    
			     if(E.equals("vm"))
			     {
				      VMFile.add(file);
				      vmcount++;         
			     }              
			 }
			       
			       
			 if(vmcount>1)
			 {
				 int count=0;
				 PrintWriter P=new PrintWriter(Wpath);	
				 String BS =VM_Translate.BootStrap();
						      
				 P.println(BS);
				 while(count<vmcount)
				 {
				    	File f=new File(VMFile.get(count).getAbsolutePath());
				        Scanner op=new Scanner(f);
				        CurrentFile=VMFile.get(count).getName();//return the element at a given index
				        while(op.hasNextLine())
				        {
				    		String line=op.nextLine();
				    		line = line.replaceAll("//.*", "").trim();
				    		String s=VM_Translate.Parse(line);
				    		P.println(s);
				    	}
				    	op.close();
				    	count+=1;
			      }
				  P.close();
			  }
			       
			 else
			 {
			       	File f=new File(VMFile.get(0).getAbsolutePath());
			       	Scanner op=new Scanner(f);
			       	PrintWriter pw=new PrintWriter(Wpath);
			        CurrentFile=VMFile.get(0).getName();
			       	while(op.hasNextLine())
			       	{
			       		String line=op.nextLine();
			       		//line = line.replaceAll("//.*", "").trim();
			       		String s=VM_Translate.Parse(line);
			       		pw.println(s);
			       	}
			       	op.close();
			       	pw.close();
			       	
			   }
		}
		catch(IOException e)  
        {  
        	System.out.println("File not Found!!!");
        }  
		 
	 }

}

