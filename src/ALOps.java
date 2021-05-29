
public class ALOps {

    static int compNum = 0;

    static String ops(String op) {
        switch (op) {
        case "add":
            return ops1(op, "M=D+M\n");

        case "sub":
            return ops1(op, "M=M-D\n");

        case "or":
            return ops1(op, "M=D|M\n");
        case "and":
            return ops1(op, "M=M&D\n");

        case "neg":
            return ops2(op, "M=-M\n");

        case "not":
            return ops2(op, "M=!M\n");

        case "eq":
            return ops3(op, "D;JEQ\n");
        case "gt":
            return ops3(op, "D;JGT\n");
        case "lt":
            return ops3(op, "D;JLT\n");
        case "return":
            return Return();
        default:
            return "";
        }
    }

    static String ops1(String op, String code) {
        // HANDLE AND, OR, ADD, SUB
        StringBuilder sb = new StringBuilder();

        sb.append("\n//" + op);
        sb.append("\n@SP");
        sb.append("\nAM=M-1");
        sb.append("\nD=M\n");
        sb.append("@SP\n");
        sb.append("AM=M-1\n");
        sb.append(code);
        sb.append("@SP\n");
        sb.append("M=M+1");

        return sb.toString();
    }

    static String ops2(String op, String code) {
        // HANDLES NOT, NEG
        StringBuilder sb = new StringBuilder();

        sb.append("\n//" + op + "\n");
        sb.append("@SP\n");
        sb.append("AM=M-1\n");
        sb.append(code);
        sb.append("@SP\n");
        sb.append("M=M+1");

        return sb.toString();
    }

    static String ops3(String op, String code) {
        // HANDLES LT, GT, EQ
        compNum++;
        StringBuilder sb = new StringBuilder();

        sb.append("\n//" + op + "\n");
        sb.append("AM=M-1\n");
        sb.append("D=M\n");
        sb.append("@SP\n");
        sb.append("AM=M-1\n");
        sb.append("D=M-D\n");
        sb.append("@TRUE" + compNum + "\n");
        sb.append(code);
        sb.append("@FALSE" + compNum + "\n");
        sb.append("0;JMP\n");
        sb.append("(TRUE" + compNum + ")\n");
        sb.append("@SP\n");
        sb.append("A=M\n");
        sb.append("M=-1\n");
        sb.append("@SP\n");
        sb.append("M=M+1\n");
        sb.append("@END");
        sb.append(compNum + "\n");
        sb.append("0;JMP\n");
        sb.append("(FALSE" + compNum + ")\n");
        sb.append("@SP\n");
        sb.append("A=M\n");
        sb.append("M=0\n");
        sb.append("@SP\n");
        sb.append("M=M+1\n");
        sb.append("(END" + compNum + ")");

        return sb.toString();
    }

    static String function(String f, int k) {
        String s = "//" + f + "\n(" + f + ")\n" + "@SP\n" + "A=M\n";
        for (int i = 0; i < k; i++) {
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
