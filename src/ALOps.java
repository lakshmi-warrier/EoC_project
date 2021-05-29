
public class ALOps {

    static int compNum = 0;

    static String add() {
        return "\n//add\n@SP\n" + "AM=M-1\n" + "D=M\n" + "@SP\n" + "AM=M-1\n" + "M=D+M\n" + "@SP\n" + "M=M+1";
    }

    static String sub() {
        return "\n//sub\n@SP\n" + "AM=M-1\n" + "D=M\n" + "@SP\n" + "AM=M-1\n" + "M=M-D\n" + "@SP\n" + "M=M+1";

    }

    static String or() {
        return "\n//or\n@SP\n" + "AM=M-1\n" + "D=M\n" + "@SP\n" + "AM=M-1\n" + "M=D|M\n" + "@SP\n" + "M=M+1";
    }

    static String and() {
        return "\n//and\n@SP\n" + "AM=M-1\n" + "D=M\n" + "@SP\nAM=M-1\n" + "M=M&D\n" + "@SP\nM=M+1";
    }

    static String gt() {
        compNum++;
        return "\n//gt\n" + "@SP\n" + "AM=M-1\n" + "D=M\n" + "@SP\n" + "AM=M-1\n" + "D=M-D\n" + "@TRUE" + compNum + "\n"
                + "D;JGT\n" + "@FALSE" + compNum + "\n" + "0;JMP\n" + "(TRUE" + compNum + ")\n" + "@SP\n" + "A=M\n"
                + "M=-1\n" + "@SP\n" + "M=M+1\n" + "@END" + compNum + "\n" + "0;JMP\n" + "(FALSE" + compNum + ")\n"
                + "@SP\n" + "A=M\n" + "M=0\n" + "@SP\n" + "M=M+1\n" + "(END" + compNum + ")";
    }

    static String lt() {
        compNum++;
        return "\n//lt\n" + "@SP\n" + "AM=M-1\n" + "D=M\n" + "@SP\n" + "AM=M-1\n" + "D=M-D\n" + "@TRUE" + compNum + "\n"
                + "D;JLT\n" + "@FALSE" + compNum + "\n" + "0;JMP\n" + "(TRUE" + compNum + ")\n" + "@SP\n" + "A=M\n"
                + "M=-1\n" + "@SP\n" + "M=M+1\n" + "@END" + compNum + "\n" + "0;JMP\n" + "(FALSE" + compNum + ")\n"
                + "@SP\n" + "A=M\n" + "M=0\n" + "@SP\n" + "M=M+1\n" + "(END" + compNum + ")";
    }

    static String neg() {
        return "\n//neg\n" + "@SP\n" + "AM=M-1\n" + "M=-M\n" + "@SP\n" + "M=M+1";
    }

    static String not() {
        return "\n//not\n" + "@SP\n" + "AM=M-1\n" + "M=!M\n" + "@SP\n" + "M=M+1";
    }

    static String eq() {
        compNum++;
        return "\n//eq\n@SP\n" + "AM=M-1\n" + "D=M\n" + "@SP\n" + "AM=M-1\n" + "D=M-D\n" + "@TRUE" + compNum + "\n"
                + "D;JEQ\n" + "@FALSE" + compNum + "\n" + "0;JMP\n" + "(TRUE" + compNum + ")\n" + "@SP\n" + "A=M\n"
                + "M=-1\n" + "@SP\n" + "M=M+1\n" + "@END" + compNum + "\n" + "0;JMP\n" + "(FALSE" + compNum + ")\n"
                + "@SP\n" + "A=M\n" + "M=0\n" + "@SP\n" + "M=M+1\n" + "(END" + compNum + ")";

    }

    static String function(String f, int k) {
        String s = "//" + f + "\n(" + f + ")\n" + "@SP\n" + "A=M\n";
        for (int i = 0; i < k; i ++) {
            s += "M=0\n" + "A=A+1\n";
        }
        return s + "D=A\n" + "@SP\n" + "M=D\n";
    }

    static String call(String f, String n) {
        compNum++;
        return "//call " + f + "\n@SP\n" + "D=M\n" + "@R13\n" + "M=D\n" + "@RET." + compNum + "\n" + "D=A\n" + "@SP\n"
                + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n" + "@LCL\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n"
                + "M=M+1\n" + "@ARG\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n" + "@THIS\n"
                + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n" + "@THAT\n" + "D=M\n" + "@SP\n" + "A=M\n"
                + "M=D\n" + "@SP\n" + "M=M+1\n" + "@R13\n" + "D=M\n" + "@" + n + "\n" + "D=D-A\n" + "@ARG\n" + "M=D\n"
                + "@SP\n" + "D=M\n" + "@LCL\n" + "M=D\n" + "@" + f + "\n" + "0;JMP\n" + "(RET." + compNum + ")\n";
    }

    static String Return() {
        return "//return\n@LCL\n" + "D=M\n" + "@5\n" + "A=D-A\n" + "D=M\n" + "@R13\n" + "M=D\n" + "@SP\n" + "A=M-1\n"
                + "D=M\n" + "@ARG\n" + "A=M\n" + "M=D \n" + "D=A+1\n" + "@SP\n" + "M=D\n" + "@LCL\n" + "AM=M-1\n"
                + "D=M\n" + "@THAT\n" + "M=D\n" + "@LCL\n" + "AM=M-1\n" + "D=M\n" + "@THIS\n" + "M=D\n" + "@LCL\n"
                + "AM=M-1\n" + "D=M\n" + "@ARG\n" + "M=D\n" + "@LCL\n" + "A=M-1\n" + "D=M\n" + "@LCL\n" + "M=D\n"
                + "@R13\n" + "A=M\n" + "0;JMP\n";

    }
}
