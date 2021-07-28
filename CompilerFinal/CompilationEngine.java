import java.io.*;

public class CompilationEngine 
{
    static VMWriter vmWriter;
    static JackTokenizer tokenizer;
    static SymbolTable symbolTable;
    static String Class;
    static String Subroutine;
    static int LabelCount;
    public CompilationEngine(File inFile, File outFile) 
    {
        tokenizer = new JackTokenizer(inFile);
        vmWriter = new VMWriter(outFile);
        symbolTable = new SymbolTable();
        LabelCount = 0;
    }
    static String Function()
    {
        if (Class.length() != 0 && Subroutine.length() !=0)
        {
            return Class + "." + Subroutine;
        }
        return "";
    }
    static String CompilerParse()
    {
        JackTokenizer.Advance();

        // check if the line is keyword
        // Also, if the keyword is int|char|boolean
        // if yes, the corresponding token is returned
        if (JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && (JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.INT || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.CHAR || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.BOOLEAN))
        {
            return JackTokenizer.CurrentToken();
        }

        // checks if the token is an identifier or not
        // if yes, the corresponding token is returned
        if (JackTokenizer.token() == JackTokenizer.TYPE.IDENTIFIER)
        {
            return JackTokenizer.Identifier();
        }
        
        // if all the above is failed, an error is being showed.
        error("in|char|boolean|className");

        return "";
    }
    public void ClassCompiler()
    {
        //checks if the code starts with class
        JackTokenizer.Advance();

        // if it dosen't starts with class, it will goes to the method error to show where the error has happened
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD || JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.CLASS)
        {
            error("class"); // goes to the method error
        }

        //checks the className
        JackTokenizer.Advance(); 

        // checks if the class name is an identifier
        // if not an identifier it shows an error
        if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
        {
            error("className");
        }

        //classname does not need to be put in symbol table
        // checks if the current token type is identifier or not
        // if it is a identifier it is being stored in the string class
        Class = JackTokenizer.Identifier();

        // checks if the next line starts with {
        Symbol('{');

        //classVarDec* subroutineDec*
        CompilerClassVarDec();
        CompileSubroutine();

        //checks if it has closed bracker '}'
        Symbol('}');

        // checks if the code have more token or not
        if (JackTokenizer.HasMoreTokens())
        {
            throw new IllegalStateException("Unexpected tokens");
        }

        //saves the file
        VMWriter.close();
    }
    static void CompilerClassVarDec()
    {
        //first determine whether there is a classVarDec, nextToken is } or start subroutineDec
        JackTokenizer.Advance();

        //checks if the next line is a }
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '}')
        {
            JackTokenizer.Pointer();
            return;
        }

        //next is start subroutine or classVariable, both start with keyword
        // checks if it is  keyword or not
        // if not a keyword, it shows an error
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD)
        {
            error("Keywords");
        }

        //next is subroutine
        // checks if the keywords are contructor| function| Method
        // if yes, it goes to the method pointer
        if (JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.CONSTRUCTOR || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.FUNCTION || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.METHOD)
        {
            JackTokenizer.Pointer();
            return;
        }

        //classVariable 
        // checks if the keyword is static or field
        // if not, it shows the error
        if (JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.STATIC && JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.FIELD)
        {
            error("static or field");
        }

        Symbol.KIND kind = null;
        String type = "";
        String name = "";

        // for class variable
        // checks if it is static or field and store it as "kind"
        switch (JackTokenizer.KeyWords())
        {
            case STATIC:
            {
                kind = Symbol.KIND.STATIC;
                break;
            }
            case FIELD:
            {   
                kind = Symbol.KIND.FIELD;
                break;
            }
        }

        // gets the type of keyword
        // if it is int|boolean|char|identifier
        // it is being stored as
        type = CompilerParse();

        //at least one varName
        while(true)
        {
            //varName
            JackTokenizer.Advance();

            // checks if the variable name is an identifier or not
            // if not a identifier, an error is shown
            if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
            {
                error("identifier");
            }

            // the identifier is stored as name
            name = JackTokenizer.Identifier();

            // the symbol,type of varibale and identifiers are added to symbol table
            SymbolTable.define(name,type,kind);

            //checks for (, or ;)
            JackTokenizer.Advance();

            // if the symbol is not , or ;
            // it shows an error
            if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || (JackTokenizer.Symbol() != ',' && JackTokenizer.Symbol() != ';'))
            {
                error("',' or ';'");
            }

            // if the symbol is ; the loop gets breaks
            if (JackTokenizer.Symbol() == ';')
            {
                break;
            }
        }
        
        // the method is being called unless ';' is being found
        CompilerClassVarDec();
    }
    static void CompileSubroutine()
    {

        //checks if the line has '}'
        JackTokenizer.Advance();

        //next is a '}', then it returns
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '}')
        {
            JackTokenizer.Pointer();
            return;
        }

        //checks if the token start with keyword
        //also, if that starts with function|constructor|method
        // if not, prints the error
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD || (JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.CONSTRUCTOR && JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.FUNCTION && JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.METHOD))
        {
            error("constructor|function|method");
        }

        JackTokenizer.KEYWORD keyword = JackTokenizer.KeyWords();

        // add to symbol table
        SymbolTable.startSubroutine();

        //for method this is the first argument
        // if the token is method, then it gets added to the hashmap subroutineSymbols in the class SymbolTable
        if (JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.METHOD)
        {
            SymbolTable.define("this",Class, Symbol.KIND.ARG); // add to symboltable
        }

        String type = "";

        //'void' or type
        JackTokenizer.Advance();

        // checks if the token is there a keyword or not
        // checks if the keyword is void or not
        if (JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.VOID)
        {
            // if the token is void, string type is being stored as void
            type = "void";
        }
        else 
        {
            // if not, it will check if the token is int|char|boolean and store it as type
            JackTokenizer.Pointer();
            type = CompilerParse();
        }

        //subroutineName which is a identifier
        JackTokenizer.Advance();

        // checks if the subroutine name is an identifier or not
        //if not, an error will be displayed
        if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
        {
            error("subroutineName");
        }

        // the identifier is being stored as string subroutine
        Subroutine = JackTokenizer.Identifier();

        //checks if there is the opening bracket '('
        Symbol('(');

        //parameterList
        ParameterList();

        //checks if there is an closing bracket ')'
        Symbol(')');

        //subroutineBody
        compileSubroutineMain(keyword); // goes to the method compileSubroutineMain()
        CompileSubroutine(); //goes to method compileSubroutine()

    }
    static void compileSubroutineMain(JackTokenizer.KEYWORD keyword)
    {
        // checks if there is the opening bracket '{'
        Symbol('{');

        //goes to method varDec
        VarDec();

        //write VM function declaration
        writeFunctionDec(keyword);

        //statements
        StatementCompile();

        //checks if it has the symbol '}'
        Symbol('}');
    }
    static void writeFunctionDec(JackTokenizer.KEYWORD keyword)
    {
        VMWriter.writeFunction(Function(),SymbolTable.varCount(Symbol.KIND.VAR));

        //METHOD and CONSTRUCTOR need to load this pointer
        if (keyword == JackTokenizer.KEYWORD.METHOD)
        {
            //A Jack method with k arguments is compiled into a VM function that operates on k + 1 arguments.
            // The first argument (argument number 0) always refers to the this object.
            VMWriter.writePush(VMWriter.SEGMENT.ARG, 0);
            VMWriter.writePop(VMWriter.SEGMENT.POINTER,0);

        }
        else if (keyword == JackTokenizer.KEYWORD.CONSTRUCTOR)
        {
            //A Jack function or constructor with k arguments is compiled into a VM function that operates on k arguments.
            VMWriter.writePush(VMWriter.SEGMENT.CONST,SymbolTable.varCount(Symbol.KIND.FIELD));
            VMWriter.writeCall("Memory.alloc", 1);
            VMWriter.writePop(VMWriter.SEGMENT.POINTER,0);
        }
    }
    static void StatementCompile()
    {
        //determine whether there is a statement next can be a '}'
        JackTokenizer.Advance();

        //next is a '}'
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '}')
        {
            JackTokenizer.Pointer();
            return;
        }

        //next is 'let'|'if'|'while'|'do'|'return'
        // if not an error is shown
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD)
        {
            error("keyword");
        }
        else 
        {
            switch (JackTokenizer.KeyWords())
            {
                // goes to the corresponding methos and the corresponding vm code is generated and written to the vm file 
                case LET:
                {
                    Let(); 
                    break;
                }
                case IF:
                {
                    If();
                    break;
                }
                case WHILE:
                {
                    While();
                    break;
                }
                case DO:
                {
                    Do();
                    break;
                }
                case RETURN:
                {
                    Return();
                    break;
                }
                default:
                {
                    // if the above keywords are not found, an error will be shown
                    error("'let'|'if'|'while'|'do'|'return'");
                }
            }
        }
        StatementCompile();
    }
    static void ParameterList()
    {
        //check if there is parameterList, if next token is ')' than go back
        JackTokenizer.Advance();
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == ')')
        {
            JackTokenizer.Pointer();
            return;
        }

        String type = "";

        //there is parameter, at least one varName
        JackTokenizer.Pointer();
        while(true)
        {
            //checks if it is int|char|boolean and stored as type
            type = CompilerParse();

            //varName
            JackTokenizer.Advance();

            //checks if the token is an identifier or not
            //if not, an error will be displayed
            if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
            {
                error("identifier");
            }

            // it being added to the hashtable subroutine symbols
            SymbolTable.define(JackTokenizer.Identifier(),type, Symbol.KIND.ARG);

            //checks if the symbol is ',' or ')' is present or not
            // if not present, an error is being shown
            JackTokenizer.Advance();
            if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || (JackTokenizer.Symbol() != ',' && JackTokenizer.Symbol() != ')'))
            {
                error("',' or ')'");
            }

            // if the symbol is ')' , it goes back 
            if (JackTokenizer.Symbol() == ')')
            {
                JackTokenizer.Pointer();
                break;
            }
        }
    }
    static void VarDec()
    {
        //determine if there is a varDec
        JackTokenizer.Advance();

        //no 'var' go back
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD || JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.VAR)
        {
            JackTokenizer.Pointer();
            return;
        }

        //checks if it is int|char|boolean and stored as type 
        String type = CompilerParse();

        while(true)
        {
            //varName
            JackTokenizer.Advance();

            // if the varname is not an identifier an error is being shown
            if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
            {
                error("identifier");
            }

            // the varname is being added to symboltable subroutine symbols
            SymbolTable.define(JackTokenizer.Identifier(),type, Symbol.KIND.VAR);

            //checks if the symbols are ',' or ';'
            // if not an error will be shown
            JackTokenizer.Advance();
            if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || (JackTokenizer.Symbol() != ',' && JackTokenizer.Symbol() != ';'))
            {
                error("',' or ';'");
            }

            // if the symbol is ';' the loop breaks
            if (JackTokenizer.Symbol() == ';')
            {
                break;
            }
        }
        // the method is being called unless ';' is being found
        VarDec();
    }
    static void Do()
    {
        //subroutineCall
        SubroutineCall();

        //checks if the ';' is present
        //if not present the corresponding error is shown
        Symbol(';');

        //pop return value
        VMWriter.writePop(VMWriter.SEGMENT.TEMP,0);
    }
    static void Let()
    {
        //varName
        JackTokenizer.Advance();

        if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
        {
            error("varName");
        }
        String varName = JackTokenizer.Identifier();

        //'[' or '='
        JackTokenizer.Advance();
        if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || (JackTokenizer.Symbol() != '[' && JackTokenizer.Symbol() != '='))
        {
            error("'['|'='");
        }
        boolean expExist = false;

        //'[' expression ']' ,need to deal with array [base+offset]
        if (JackTokenizer.Symbol() == '[')
        {
            expExist = true;

            //push array variable,base address into stack
            VMWriter.writePush(segment(SymbolTable.kindOf(varName)),SymbolTable.indexOf(varName));

            //calc offset
            Expression();

            //']'
            Symbol(']');

            //base+offset
            VMWriter.writeArithmetic(VMWriter.COMMAND.ADD);
        }

        if (expExist) JackTokenizer.Advance();

        //expression
        Expression();

        //';'
        Symbol(';');

        if (expExist)
        {
            //*(base+offset) = expression
            //pop expression value to temp
            VMWriter.writePop(VMWriter.SEGMENT.TEMP,0);

            //pop base+index into 'that'
            VMWriter.writePop(VMWriter.SEGMENT.POINTER,1);

            //pop expression value into *(base+index)
            VMWriter.writePush(VMWriter.SEGMENT.TEMP,0);
            VMWriter.writePop(VMWriter.SEGMENT.THAT,0);
        }
        else 
        {
            //pop expression value directly
            VMWriter.writePop(segment(SymbolTable.kindOf(varName)), SymbolTable.indexOf(varName));
        }
    }
    static VMWriter.SEGMENT segment(Symbol.KIND kind)
    {
        // the corresponding vm code is being written to the vm file
        switch (kind)
        {
            case FIELD:
            {
                return VMWriter.SEGMENT.THIS;
            }
            case STATIC:
            {
                return VMWriter.SEGMENT.STATIC;
            }
            case VAR:
            {
                return VMWriter.SEGMENT.LOCAL;
            }
            case ARG:
            {
                return VMWriter.SEGMENT.ARG;
            }
            default:
            {
                return VMWriter.SEGMENT.NONE;
            }
        }
    }
    static void While()
    {

        String continueLabel = Label();
        String topLabel = Label();

        //top label for while loop
        VMWriter.writeLabel(topLabel);

        //checks if the opening bracket is present or not
        Symbol('(');

        //expression while condition: true or false
        Expression();

        //checks if the closing bracket is present or not
        Symbol(')');

        //if ~(condition) go to continue label
        VMWriter.writeArithmetic(VMWriter.COMMAND.NOT);
        VMWriter.writeIf(continueLabel);

        //checks if the opening bracket is present or not
        Symbol('{');

        //statements
        StatementCompile();

        //checks if the closing bracket is present or not
        Symbol('}');

        //if (condition) go to top label
        VMWriter.writeGoto(topLabel);

        //or continue
        VMWriter.writeLabel(continueLabel);
    }

    static String Label()
    {
        return "LABEL_" + (LabelCount++);
    }
    static void Return()
    {
        //check if there is any expression
        JackTokenizer.Advance();
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == ';')
        {
            //no expression push 0 to stack
            VMWriter.writePush(VMWriter.SEGMENT.CONST,0);
        }
        else 
        {
            //expression exist
            JackTokenizer.Pointer();

            //expression
            Expression();

            //checks if the symbol ';' is present
            Symbol(';');
        }
        // the vm code for return is being written to the vm file
        VMWriter.writeReturn();
    }
    static void If()
    {
        String elseLabel = Label();
        String endLabel = Label();

        //checks if the opening bracket is present or not
        Symbol('(');

        //expression
        Expression();

        //checks if the closing bracket is present or not
        Symbol(')');

        //if ~(condition) go to else label
        VMWriter.writeArithmetic(VMWriter.COMMAND.NOT);
        VMWriter.writeIf(elseLabel);

        //checks if the opening bracket is present or not
        Symbol('{');

        //statements
        StatementCompile();

        //checks if the closing bracket is present or not
        Symbol('}');

        //if condition after statement finishing, go to end label
        VMWriter.writeGoto(endLabel);

        //check if there is 'else'
        VMWriter.writeLabel(elseLabel);
        
        JackTokenizer.Advance();
        if (JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.ELSE)
        {
            //checks if the opening bracket is present or not
            Symbol('{');

            //statements
            StatementCompile();

            //checks if the closing bracket is present or not
            Symbol('}');
        }
        else 
        {
            JackTokenizer.Pointer();
        }
        VMWriter.writeLabel(endLabel);
    }
    static void Term()
    {
        JackTokenizer.Advance();

        //check if it is an identifier
        if (JackTokenizer.token() == JackTokenizer.TYPE.IDENTIFIER)
        {
            //varName|varName '[' expression ']'|subroutineCall
            String tempId = JackTokenizer.Identifier();

            JackTokenizer.Advance();
            if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '[')
            {
                //this is an array entry
                //push array variable,base address into stack
                VMWriter.writePush(segment(SymbolTable.kindOf(tempId)),SymbolTable.indexOf(tempId));

                //expression
                Expression();

                //checks if the closing bracket is present or not
                Symbol(']');

                //base+offset
                VMWriter.writeArithmetic(VMWriter.COMMAND.ADD);

                //pop into 'that' pointer
                VMWriter.writePop(VMWriter.SEGMENT.POINTER,1);

                //push *(base+index) onto stack
                VMWriter.writePush(VMWriter.SEGMENT.THAT,0);
            }
            else if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && (JackTokenizer.Symbol() == '(' || JackTokenizer.Symbol() == '.'))
            {
                //this is a subroutineCall
                JackTokenizer.Pointer();
                JackTokenizer.Pointer();
                SubroutineCall();
            }
            else 
            {
                //this is varName
                JackTokenizer.Pointer();

                //push variable directly onto stack
                VMWriter.writePush(segment(SymbolTable.kindOf(tempId)), SymbolTable.indexOf(tempId));
            }
        }
        else
        {
            //integerConstant|stringConstant|keywordConstant|'(' expression ')'|unaryOp term
            if (JackTokenizer.token() == JackTokenizer.TYPE.INTEGERCONSTANT)
            {
                //integerConstant just push its value onto stack
                VMWriter.writePush(VMWriter.SEGMENT.CONST,JackTokenizer.IntegerValue());
            }
            else if (JackTokenizer.token() == JackTokenizer.TYPE.STRINGCONSTANT)
            {
                //stringConstant new a string and append every char to the new stack
                String str = JackTokenizer.StringValue();

                VMWriter.writePush(VMWriter.SEGMENT.CONST,str.length());
                VMWriter.writeCall("String.new",1);

                for (int i = 0; i < str.length(); i++)
                {
                    VMWriter.writePush(VMWriter.SEGMENT.CONST,(int)str.charAt(i));
                    VMWriter.writeCall("String.appendChar",2);
                }
            }
            else if(JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.TRUE)
            {
                //~0 is true
                VMWriter.writePush(VMWriter.SEGMENT.CONST,0);
                VMWriter.writeArithmetic(VMWriter.COMMAND.NOT);
            }
            else if(JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.THIS)
            {
                //push this pointer onto stack
                VMWriter.writePush(VMWriter.SEGMENT.POINTER,0);
            }
            else if(JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && (JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.FALSE || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.NULL))
            {
                //0 for false and null
                VMWriter.writePush(VMWriter.SEGMENT.CONST,0);
            }
            else if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '(')
            {
                //expression
                Expression();

                //checks if the closing bracket is present or not
                Symbol(')');
            }
            else if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && (JackTokenizer.Symbol() == '-' || JackTokenizer.Symbol() == '~'))
            {
                char s = JackTokenizer.Symbol();

                //term
                Term();
                
                if (s == '-')
                {
                    VMWriter.writeArithmetic(VMWriter.COMMAND.NEG);
                }
                else 
                {
                    VMWriter.writeArithmetic(VMWriter.COMMAND.NOT);
                }

            }
            else
            {
                error("integerConstant|stringConstant|keywordConstant|'(' expression ')'|unaryOp term");
            }
        }
    }
    static void SubroutineCall()
    {
        JackTokenizer.Advance();

        // checks if the token is an identifier or not
        // if not, an error will be shown
        if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
        {
            error("identifier");
        }

        //the identifier is stored as a string name
        String name = JackTokenizer.Identifier();

        int nArgs = 0;

        JackTokenizer.Advance();

        //checks if the token is a symbol or not
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '(')
        {
            //push this pointer
            VMWriter.writePush(VMWriter.SEGMENT.POINTER,0);

            //'(' expressionList ')'
            //expressionList
            nArgs = ExpressionList() + 1;

            //')'
            Symbol(')');

            //call subroutine
            VMWriter.writeCall(Class + '.' + name, nArgs);
        }
        else if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '.')
        {
            //(className|varName) '.' subroutineName '(' expressionList ')'
            String objName = name;

            //subroutineName
            JackTokenizer.Advance();
            
            if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
            {
                error("identifier");
            }
            name = JackTokenizer.Identifier();

            //check for if it is built-in type
            String type = SymbolTable.typeOf(objName);

            if (type.equals("int")||type.equals("boolean")||type.equals("char")||type.equals("void"))
            {
                error("no built-in type");
            }
            else if (type.equals(""))
            {
                name = objName + "." + name;
            }
            else 
            {
                nArgs = 1;

                //push variable directly onto stack
                VMWriter.writePush(segment(SymbolTable.kindOf(objName)), SymbolTable.indexOf(objName));
                name = SymbolTable.typeOf(objName) + "." + name;
            }

            //checks if the opening bracket is present or not
            Symbol('(');

            //expressionList
            nArgs += ExpressionList();

            //checks if the closing bracket is present or not
            Symbol(')');

            //call subroutine
            VMWriter.writeCall(name,nArgs);
        }
        else // shows an error 
        {
            error("'('|'.'");
        }
    }
    static void Expression() // to write the vm command add, sub, gt, lt into the vm file
    {
        // the corresponding vm codes are being written onto the vm file of the terms
        Term();
        
        //(op term)*
        while (true)
        {
            JackTokenizer.Advance();

            //checks if the token is a symbol and an operation
            if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.IsOperation())
            {
                String opCmd = "";
                switch (JackTokenizer.Symbol()) // checks the symbols
                {
                    case '+':
                    {
                        opCmd = "add";
                        break;
                    }
                    case '-':
                    {
                        opCmd = "sub";
                        break;
                    }
                    case '*':
                    {
                        opCmd = "call Math.multiply 2";
                        break;
                    }
                    case '/':
                    {
                        opCmd = "call Math.divide 2";
                        break;
                    }
                    case '<':
                    {
                        opCmd = "lt";
                        break;
                    }
                    case '>':
                    {
                        opCmd = "gt";
                        break;
                    }
                    case '=':
                    {
                        opCmd = "eq";
                        break;
                    }
                    case '&':
                    {
                        opCmd = "and";
                        break;
                    }
                    case '|':
                    {
                        opCmd = "or";
                        break;
                    }
                    default:
                    {
                        error("Unknown op!");
                        break;
                    }
                }

                //term
                Term();

                //write the corresponding vm command to vm file
                VMWriter.writeCommand(opCmd,"","");
            }
            else  // breaks the loop
            {
                JackTokenizer.Pointer();
                break;
            }
        }
    }
    static int ExpressionList()
    {
        int nArgs = 0;
        JackTokenizer.Advance();

        //determine if there is any expression, if next is ')' then no
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == ')')
        {
            JackTokenizer.Pointer();
        }
        else 
        {
            nArgs = 1;
            JackTokenizer.Pointer();

            //if an operation like +|-|.... the corresponding vm codes are written onto the vm file
            Expression();
            
            //(','expression)*
            while (true) 
            {
                JackTokenizer.Advance();
                // checks if the token is a symbol and a ','
                if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == ',')
                {
                    //if an operation like +|-|.... the corresponding vm codes are written onto the vm file
                    Expression();
                    nArgs++;
                }
                else 
                {
                    //else breaks the loop
                    JackTokenizer.Pointer();
                    break;
                }
            }
        }
        return nArgs;
    }
    static void error(String val)
    {
        //to print the error statement
        throw new IllegalStateException("Expected token missing : " + val + " Current token:" + JackTokenizer.CurrentToken());
    }
    static void Symbol(char symbol)
    {
        JackTokenizer.Advance();
        // cheks if the symbol is there in the in symbolReg
        if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || JackTokenizer.Symbol() != symbol)
        {
            // if not in the symbolReg it shows an error
            error("'" + symbol + "'");
        }
    }
}