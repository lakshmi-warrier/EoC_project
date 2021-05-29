import java.util.*;

import java.io.*;

class Translator {
    static String outFilePath;
    static String outFileName;
    static String currFileName;

    public static String translatorMain(String inFolderPath) {

        ArrayList<File> VMfiles = new ArrayList<>();// collection of all the vm files
        File in = new File(inFolderPath);
        if (in.isDirectory()) {
            File[] files = in.listFiles();
            for (File file : files) {
                String path = file.getAbsolutePath();
                try {
                    String extension = path.substring(path.lastIndexOf("."));
                    if (extension.equals(".vm"))
                        VMfiles.add(file);
                } catch (StringIndexOutOfBoundsException e) {
                    // if the folder contains no files, an index out of bound exception is thrown
                    // bcz of missing '.'
                    System.out.println("Can't find any files in the path. Please check the path\nClosing program");
                    System.exit(0);

                }
            }
        } else {
            System.out.println("Not a directory, abort");
            System.exit(0);
        }

        outFileName = in.getName();
        outFilePath = inFolderPath + "\\" + outFileName + ".asm";

        String asm = "";
        // no VM files
        if (VMfiles.size() == 0) {
            System.out.println("Can't find any VM files in the directory mentioned. \nClosing program");
            System.exit(0);
        } else {
            // bootstrap code
            asm = "@256\n" + "D=A\n" + "@SP\n" + "M=D\n" + ALOps.call("Sys.init", "0") + "0;JMP\n";

            int count = 0;
            int vmcount = VMfiles.size();

            while (count < vmcount) {
                File f = new File(VMfiles.get(count).getAbsolutePath());
                currFileName = f.getName();
                currFileName = currFileName.substring(0, currFileName.length() - 3);
                Scanner sc;
                try {
                    sc = new Scanner(f);

                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        line = string_cleaner(line);
                        String code = code_generator(line).trim();
                        if (code != null)
                            asm += code + "\n";

                    }
                    sc.close();
                    // System.out.println(asm);
                    count++;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        write_file(asm.trim());
        System.out.println("\n------ ASM File saved at: " + outFilePath + " ------");

        return outFilePath;
    }

    static void write_file(String asm) {
        File outf = new File(outFilePath);
        try {
            PrintWriter pw = new PrintWriter(outf);
            pw.println(asm.trim());

            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Output file not found");
        }
    }

    static String string_cleaner(String s) {
        String newString = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '/')
                break;
            else
                newString += s.charAt(i);
        }

        return newString.strip();
    }

    static String code_generator(String VM) {
        String[] VMcommand = VM.split(" "); // To Handle push and pop segment i

        String op = VMcommand[0];
        if (VMcommand.length == 1) {
            if (op.equals("add"))
                return (ALOps.add());
            else if (op.equals("sub"))
                return (ALOps.sub());
            else if (op.equals("or"))
                return (ALOps.or());
            else if (op.equals("and"))
                return (ALOps.and());
            else if (op.equals("eq"))
                return (ALOps.eq());
            else if (op.equals("neg"))
                return (ALOps.neg());
            else if (op.equals("lt"))
                return (ALOps.lt());
            else if (op.equals("gt"))
                return (ALOps.gt());
            else if (op.equals("not"))
                return (ALOps.not());
            else if (op.equals("return"))
                return ALOps.Return();
        } else {
            String segment = VMcommand[1];

            if (VMcommand.length == 3) {
                String i = VMcommand[2];

                if (op.equals("push")) {
                    if (segment.equals("constant"))
                        return (push_const(Integer.parseInt(i)));
                    else {
                        if (segment.equals("argument"))
                            return (push_arg(Integer.parseInt(i)));
                        else if (segment.equals("local"))
                            return (push_loc(Integer.parseInt(i)));
                        else if (segment.equals("this"))
                            return (push_this(Integer.parseInt(i)));
                        else if (segment.equals("that"))
                            return (push_that(Integer.parseInt(i)));
                        else if (segment.equals("static"))
                            return (push_static(Integer.parseInt(i)));
                        else if (segment.equals("pointer"))
                            return (push_ptr(Integer.parseInt(i)));
                        else if (segment.equals("temp"))
                            return (push_temp(Integer.parseInt(i)));
                        else
                            return "No segment" + segment + "found";
                    }
                } else if (VMcommand[0].equals("pop")) {
                    int ind = Integer.parseInt(i);

                    switch (segment) {
                        case "argument":
                            return pop_arg(ind);

                        case "local":
                            return pop_loc(ind);

                        case "this":
                            return pop_this(ind);

                        case "that":
                            return pop_that(ind);
                        
                        case "static":
                            return pop_static(ind);

                        case "pointer":
                            return pop_ptr(ind);

                        case "temp":
                            return pop_temp(ind);

                        default:
                            return " no segment "+segment+" found";
                    }
                }

                    else if (VMcommand[0].equals("function"))
                    return ALOps.function(segment, Integer.parseInt(VMcommand[2]));
                else if (VMcommand[0].equals("call"))
                    return ALOps.call(segment, VMcommand[2]);

            } else if (VMcommand[0].equals("if-goto")) {
                return "\n//if go to " + segment + "\n@SP\nAM=M-1\nD=M\n@" + segment + "\nD;JNE\n";
            } else if (VMcommand[0].equals("goto")) {
                return "@" + segment + "\n" + "0;JMP\n";
            } else if (VMcommand[0].equals("label"))
                return ("(" + segment + ")");

        }
        return "";
    }

    // --------push statements ---------

    static String push_const(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push constant " + index);
        sb.append("\n@" + index);
        sb.append("\nD=A");
        sb.append("\n@SP");
        sb.append("\nA=M");
        sb.append("\nM=D");
        sb.append("\n@SP");
        sb.append("\nM=M+1");

        System.out.println(sb.toString());
        return sb.toString();
    }

    static String push_arg(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push argument " + index);
        sb.append("\n@ARG");
        sb.append("\nD=M");
        sb.append("\n@" + index);

        return push_final(sb);
    }

    static String push_loc(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push local " + index);
        sb.append("\n@LCL");
        sb.append("\nD=M");
        sb.append("\n@" + index);

        return push_final(sb);
    }

    static String push_this(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push this " + index);
        sb.append("\n@THIS");
        sb.append("\nD=M");
        sb.append("\n@" + index);

        return push_final(sb);
    }

    static String push_that(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push that " + index);
        sb.append("\n@THAT");
        sb.append("\nD=M");
        sb.append("\n@" + index);

        return push_final(sb);
    }

    static String push_static(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push static " + index);
        sb.append("\n@" + currFileName + ".static." + index);
        sb.append("\nD=M");
        sb.append("\n@SP");
        sb.append("\nA=M");
        sb.append("\nM=D");
        sb.append("\n@SP");
        sb.append("\nM=M+1");

        return sb.toString();    }

    static String push_ptr(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push pointer " + index);
        sb.append("\n@3");
        sb.append("\nD=A");
        sb.append("\n@" + index);

        return push_final(sb);
    }

    static String push_temp(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//push temp " + index);
        sb.append("\n@5");
        sb.append("\nD=A");
        sb.append("\n@" + index);

        return push_final(sb);
    }

    static String push_final(StringBuilder sb) {
        // appends the common lines

        sb.append("\nA=D+A");
        sb.append("\nD=M");
        sb.append("\n@SP");
        sb.append("\nA=M");
        sb.append("\nM=D");
        sb.append("\n@SP");
        sb.append("\nM=M+1");

        return sb.toString();
    }

    // --------pop statements ---------
    static String pop_arg(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//pop arugument " + index);
        sb.append("\n@ARG");
        sb.append("\n@" + index);
        sb.append("\nD=D+A");

        return pop_final(sb);
    }

    static String pop_loc(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//pop local " + index);
        sb.append("\n@LCL");
        sb.append("\n@" + index);
        sb.append("\nD=D+A");

        return pop_final(sb);
    }

    static String pop_this(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//pop this " + index);
        sb.append("\n@THIS");
        sb.append("\nD=M");
        sb.append("\n@" + index);
        sb.append("\nD=D+A");

        return pop_final(sb);
    }

    static String pop_that(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//pop that " + index);
        sb.append("\n@THAT");
        sb.append("\nD=M");
        sb.append("\n@" + index);
        sb.append("\nD=D+A");

        return pop_final(sb);
    }

    static String pop_static(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//pop static " + index);
        sb.append("\n@" + currFileName + ".static." + index + "\n");
        sb.append("D=A\n");

        return pop_final(sb);
    }

    static String pop_ptr(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//pop pointer " + index);
        sb.append("\n@3");
        sb.append("\nD=A");
        sb.append("\n@" + index);
        sb.append("\nD=D+A");

        return pop_final(sb);
    }

    static String pop_temp(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("//pop temp " + index);
        sb.append("\n@5");
        sb.append("\nD=A");
        sb.append("\n@" + index);
        sb.append("\nD=D+A");

        return pop_final(sb);
    }

    static String pop_final(StringBuilder sb) {
        // appends the common lines
        sb.append("\n@R14\n");
        sb.append("M=D\n");
        sb.append("@SP\n");
        sb.append("AM=M-1\n");
        sb.append("D=M\n");
        sb.append("@R14\n");
        sb.append("A=M\n");
        sb.append("M=D\n");

        return sb.toString();
    }

}