import java.util.*;

public class Tables {

    Hashtable<String, String> jumpMnemonics = new Hashtable<String, String>();
    Hashtable<String, String> compMnemonics = new Hashtable<String, String>();
    Hashtable<String, String> destMnemonics = new Hashtable<String, String>();
    Hashtable<String, String> symbolTable = new Hashtable<String, String>();

    Tables() {
        fillCompTable();
        fillDestTable();
        fillJmpTable();
        fillSymbolTables();
    }

    void fillDestTable() {
        destMnemonics.put("NULL", "000");
        destMnemonics.put("M", "001");
        destMnemonics.put("D", "010");
        destMnemonics.put("MD", "011");
        destMnemonics.put("A", "100");
        destMnemonics.put("AM", "101");
        destMnemonics.put("AD", "110");
        destMnemonics.put("AMD", "111");
    }

    void fillCompTable() {
        compMnemonics.put("0", "0101010");
        compMnemonics.put("1", "0111111");
        compMnemonics.put("-1", "0111010");
        compMnemonics.put("D", "0001100");
        compMnemonics.put("A", "0110000");
        compMnemonics.put("M", "1110000");
        compMnemonics.put("!D", "0001101");
        compMnemonics.put("!A", "0110001");
        compMnemonics.put("!M", "1110001");
        compMnemonics.put("-D", "0001111");
        compMnemonics.put("-A", "0110011");
        compMnemonics.put("-M", "1110011");
        compMnemonics.put("D+1", "0011111");
        compMnemonics.put("A+1", "0110111");
        compMnemonics.put("M+1", "1110111");
        compMnemonics.put("D-1", "0001110");
        compMnemonics.put("A-1", "0110010");
        compMnemonics.put("M-1", "1110010");
        compMnemonics.put("D+A", "0000010");
        compMnemonics.put("D+M", "1000010");
        compMnemonics.put("D-A", "0010011");
        compMnemonics.put("D-M", "1010011");
        compMnemonics.put("A-D", "0000111");
        compMnemonics.put("M-D", "1000111");
        compMnemonics.put("D&A", "0000000");
        compMnemonics.put("D&M", "1000000");
        compMnemonics.put("D|A", "0010101");
        compMnemonics.put("D|M", "1010101");
    }

    void fillJmpTable() {
        jumpMnemonics.put("NULL", "000");
        jumpMnemonics.put("JGT", "001");
        jumpMnemonics.put("JEQ", "010");
        jumpMnemonics.put("JGE", "011");
        jumpMnemonics.put("JLT", "100");
        jumpMnemonics.put("JNE", "101");
        jumpMnemonics.put("JLE", "110");
        jumpMnemonics.put("JMP", "111");
    }

    void fillSymbolTables() {
        symbolTable.put("SP", "000000000000000");
        symbolTable.put("LCL", "000000000000001");
        symbolTable.put("ARG", "000000000000010");
        symbolTable.put("THIS", "000000000000011");
        symbolTable.put("THAT", "000000000000100");
        symbolTable.put("R0", "000000000000000");
        symbolTable.put("R1", "000000000000001");
        symbolTable.put("R2", "000000000000010");
        symbolTable.put("R3", "000000000000011");
        symbolTable.put("R4", "000000000000100");
        symbolTable.put("R5", "000000000000101");
        symbolTable.put("R6", "000000000000110");
        symbolTable.put("R7", "000000000000111");
        symbolTable.put("R8", "000000000001000");
        symbolTable.put("R9", "000000000001001");
        symbolTable.put("R10", "000000000001010");
        symbolTable.put("R11", "000000000001011");
        symbolTable.put("R12", "000000000001100");
        symbolTable.put("R13", "000000000001101");
        symbolTable.put("R14", "000000000001110");
        symbolTable.put("R15", "000000000001111");
        symbolTable.put("KBD", "110000000000000");
        symbolTable.put("SCREEN", "100000000000000");

    }
}
