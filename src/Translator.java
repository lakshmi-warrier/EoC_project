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
                    System.out.println("Can't find any files in the path. Please check the path\nClosing program");
                    System.exit(0);

                }
            }
        }

        outFileName = in.getName();
        outFilePath = inFolderPath + "\\" + outFileName + ".asm";

        String asm = "";
        // no VM files
        if (VMfiles.size() == 0) {
            System.out.println("Can't find any VM files in the directory mentioned. \nClosing program");
            System.exit(0);

        }
        if (VMfiles.size() >= 1) {
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
                            return (push(segment, Integer.parseInt(i)));
                    }
                } else if (VMcommand[0].equals("pop")) {
                    if (segment.equals("argument"))
                        return (pop_arg(Integer.parseInt(i)));
                    else if (segment.equals("local"))
                        return (pop_loc(Integer.parseInt(i)));
                    else if (segment.equals("this"))
                        return (pop_this(Integer.parseInt(i)));
                    else if (segment.equals("that"))
                        return (pop_that(Integer.parseInt(i)));
                    else if (segment.equals("static"))
                        return (pop_static(Integer.parseInt(i)));
                    else if (segment.equals("pointer"))
                        return (pop_ptr(Integer.parseInt(i)));
                    else if (segment.equals("temp"))
                        return (pop_temp(Integer.parseInt(i)));
                    else
                        return (push(segment, Integer.parseInt(i)));
                } else if (VMcommand[0].equals("function"))
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

    static String push(String segment, int index) {

        String code = "//push " + segment + " " + index + "\n@" + index + "\nD=A\n";

        return code + "@" + segment + "\n" + "A=M\n" + "D=D+A\nA=D\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n"
                + "M=M+1\n";
    }

    static String push_const(int index) {

        return "//push constant " + index + "\n@" + index + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1";

    }

    static String push_arg(int index) {
        return "//push arugument " + index + "\n@ARG\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
    }

    static String push_loc(int index) {
        return "//push local " + index + "\n@LCL\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
    }

    static String push_this(int index) {
        return "//push this " + index + "\n@THIS\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
    }

    static String push_that(int index) {
        return "//push that " + index + "\n@THAT\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
    }

    static String push_static(int index) {
        return "//push static " + index + "\n@" + currFileName + ".static." + index
                + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
    }

    static String push_ptr(int index) {
        return "//push pointer " + index + "\n@3\nD=A\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
    }

    static String push_temp(int index) {
        return "//push temp " + index + "\n@5\nD=A\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
    }

    static String pop_arg(int index) {
        return "//pop arugument " + index + "\n@ARG\nD=M\n@" + index + "\nD=D+A\n" + "@R14\n" + "M=D\n" + "@SP\n"
                + "AM=M-1\n" + "D=M\n" + "@R14\n" + "A=M\n" + "M=D\n";
    }

    static String pop_loc(int index) {
        return "//pop local " + index + "\n@LCL\nD=M\n@" + index + "\nD=D+A\n" + "@R14\n" + "M=D\n" + "@SP\n"
                + "AM=M-1\n" + "D=M\n" + "@R14\n" + "A=M\n" + "M=D\n";
    }

    static String pop_this(int index) {
        return "//pop this " + index + "\n@THIS\nD=M\n@" + index + "\nD=D+A\n" + "@R14\n" + "M=D\n" + "@SP\n"
                + "AM=M-1\n" + "D=M\n" + "@R14\n" + "A=M\n" + "M=D\n";
    }

    static String pop_that(int index) {
        return "//pop that " + index + "\n@THAT\nD=M\n@" + index + "\nD=D+A\n" + "@R14\n" + "M=D\n" + "@SP\n"
                + "AM=M-1\n" + "D=M\n" + "@R14\n" + "A=M\n" + "M=D\n";
    }

    static String pop_static(int index) {
        return "//pop static " + index + "\n@" + currFileName + ".static." + index + "\n" + "D=A\n" + "@R14\n" + "M=D\n"
                + "@SP\n" + "AM=M-1\n" + "D=M\n" + "@R14\n" + "A=M\n" + "M=D\n";
    }

    static String pop_ptr(int index) {
        return "//pop pointer " + index + "\n@3\nD=A\n@" + index + "\nD=D+A\n" + "@R14\n" + "M=D\n" + "@SP\n"
                + "AM=M-1\n" + "D=M\n" + "@R14\n" + "A=M\n" + "M=D\n";
    }

    static String pop_temp(int index) {
        return "//pop temp " + index + "\n@5\nD=A\n@" + index + "\nD=D+A\n" + "@R14\n" + "M=D\n" + "@SP\n" + "AM=M-1\n"
                + "D=M\n" + "@R14\n" + "A=M\n" + "M=D\n";
    }

}