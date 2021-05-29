/*
Enter absolute path of asm/hack file
read file
big loop that runs while !hasNextLine

read line by line, use hashtable, convert A to C - string manipulation

save the code to a new .hack file
*/

import java.io.*;
import java.util.*;

class Parser {

    Tables table = new Tables();
    
    Hashtable<String, String> symbolTable = table.symbolTable;
    Hashtable<String, String> jumpMnemonics = table.jumpMnemonics;
    Hashtable<String, String> compMnemonics = table.compMnemonics;
    Hashtable<String, String> destMnemonics = table.destMnemonics;

    String source_file_path;
    File source_file;
    int last_variable = 15;

    Parser(String source_file) {
        this.source_file_path = source_file;
        this.source_file = new File(source_file_path);
    }

    String translate() {
        String final_code = "";
        try {
            file_formatter();

            Scanner fr = new Scanner(source_file);

            while (fr.hasNextLine()) {
                String line = fr.nextLine();

                String new_line = parse(line);
                if (!new_line.isEmpty())
                    final_code += new_line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return final_code.trim();
    }

    void file_formatter() {
        String clean_fileString = "";
        int counter = 0;

        try {
            Scanner fr = new Scanner(source_file);

            while (fr.hasNextLine()) {
                String line = fr.nextLine();
                String code = cleanCode(line);

                if (!code.isEmpty()) {
                    clean_fileString += code + "\n";
                    if (code.startsWith("(")) {
                        handle_label(code.substring(1, code.length() - 1), counter);
                    } else
                        counter++;
                }
            }

            FileWriter filew = new FileWriter(source_file);

            filew.write(clean_fileString);
            filew.close();
            fr.close();
        } catch (FileNotFoundException e) {
            System.out.println("Invalid File");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    String cleanCode(String code) {
        String clean_code = "";
        for (int i = 0; i < code.length(); i++) {
            // comments begin with / - avoids in-line comments to be part of clean code

            if (code.charAt(i) == '/')
                break;
            if (code.charAt(i) != ' ')
                clean_code += code.charAt(i);
        }

        clean_code = clean_code.trim();
        return clean_code;
    }

    void handle_label(String name, int line_num)// invoked while first pass only
    {
        if (symbolTable.get(name) == null)
            symbolTable.put(name, get_15_bit_binary(line_num));
    }

    String parse(String line) {
        String code = "";
        // mnemonic A instruction: @value; in binary: 0binaryFormOfValue
        if (line.startsWith("@")) {

            // cutting @ off for simplicity
            String code_line = line.substring(1, line.length());
            code = convert_A_inst(code_line);
        } else if (line.startsWith("(")) {
            // do nothing
        } else {
            code = convert_C_inst(line);
        }
        // System.out.println(code);
        return code.trim();
    }

    String convert_A_inst(String code_line) {
        String binary = "";
        try {
            int int_inst = Integer.parseInt(code_line);
            binary = get_15_bit_binary(int_inst);

        } catch (NumberFormatException e) {
            if (symbolTable.get(code_line) == null)
                symbolTable.put(code_line, get_15_bit_binary(++last_variable));
            binary = symbolTable.get(code_line);
        }

        // making sure the string has only 15 bits
        binary = binary.substring(binary.length() - 15);

        // adding the opcode of A instruction, i.e, 0
        binary = 0 + binary;
        return binary;
    }

    String convert_C_inst(String code_line) {
        String code = "111"; // the op-code

        // C_inst syntax: dest = comp;jump
        String dest = "NULL", comp = "NULL", jump = "NULL";

        int locDestEnd = code_line.indexOf("=");
        int locCompEnd = code_line.indexOf(";");

        // dest = comp
        if (locDestEnd != -1 && locCompEnd == -1) {
            dest = code_line.substring(0, locDestEnd);
            comp = code_line.substring(locDestEnd + 1, code_line.length());
        }

        // comp;jmp
        if (locCompEnd != -1 && locDestEnd == -1) {
            comp = code_line.substring(locDestEnd + 1, locCompEnd);
            jump = code_line.substring(locCompEnd + 1, code_line.length());
        }

        // dest = comp; or dest = comp;jmp
        if (locDestEnd != -1 && locCompEnd != -1) {
            dest = code_line.substring(0, locDestEnd);
            comp = code_line.substring(locDestEnd + 1, locCompEnd);
            jump = code_line.substring(locCompEnd + 1, code_line.length());
        }

        dest = destMnemonics.get(dest);
        comp = compMnemonics.get(comp);
        jump = jumpMnemonics.get(jump);
        code = code + comp + dest + jump;

        return code;
    }

    String get_15_bit_binary(int num) {
        String binary = Integer.toBinaryString(num);

        // padding 0 to the binary string obtained so that its length=15
        while (binary.length() < 15) {
            binary = 0 + binary;
        }

        return binary;
    }
}