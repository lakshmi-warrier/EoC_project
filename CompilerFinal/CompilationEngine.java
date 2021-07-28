import java.io.*;
public class CompilationEngine 
{
    static VMWriter vmWriter;
    static JackTokenizer tokenizer;
    static SymbolTable symbolTable;
    static String Class;
    static String Subroutine;
    static int nArgs = 0;
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
        if (JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && (JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.INT || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.CHAR || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.BOOLEAN))
        {
            return JackTokenizer.CurrentToken();
        }
        if (JackTokenizer.token() == JackTokenizer.TYPE.IDENTIFIER)
        {
            return JackTokenizer.Identifier();
        }
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

        //checks if it has closed bracker }
        Symbol('}');

        // checks if the code have more token or not
        if (JackTokenizer.HasMoreTokens())
        {
            throw new IllegalStateException("Unexpected tokens");
        }

        //save file
        VMWriter.close();
    }
    static void CompilerClassVarDec()
    {
        //first determine whether there is a classVarDec, nextToken is } or start subroutineDec
        JackTokenizer.Advance();

        //next is a '}'
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '}')
        {
            JackTokenizer.Pointer();
            return;
        }

        //next is start subroutineDec or classVarDec, both start with keyword
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD)
        {
            error("Keywords");
        }

        //next is subroutineDec
        if (JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.CONSTRUCTOR || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.FUNCTION || JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.METHOD)
        {
            JackTokenizer.Pointer();
            return;
        }

        //classVarDec exists
        if (JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.STATIC && JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.FIELD)
        {
            error("static or field");
        }

        Symbol.KIND kind = null;
        String type = "";
        String name = "";

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

        //type
        type = CompilerParse();

        //at least one varName
        while(true)
        {
            //varName
            JackTokenizer.Advance();
            if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
            {
                error("identifier");
            }

            name = JackTokenizer.Identifier();

            SymbolTable.define(name,type,kind);

            //',' or ';'
            JackTokenizer.Advance();

            if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || (JackTokenizer.Symbol() != ',' && JackTokenizer.Symbol() != ';'))
            {
                error("',' or ';'");
            }
            if (JackTokenizer.Symbol() == ';')
            {
                break;
            }
        }
        CompilerClassVarDec();
    }
    static void CompileSubroutine()
    {

        //determine whether there is a subroutine, next can be a '}'
        JackTokenizer.Advance();

        //next is a '}'
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == '}')
        {
            JackTokenizer.Pointer();
            return;
        }

        //start of a subroutine
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD || (JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.CONSTRUCTOR && JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.FUNCTION && JackTokenizer.KeyWords() != JackTokenizer.KEYWORD.METHOD))
        {
            error("constructor|function|method");
        }

        JackTokenizer.KEYWORD keyword = JackTokenizer.KeyWords();
        SymbolTable.startSubroutine();

        //for method this is the first argument
        if (JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.METHOD)
        {
            SymbolTable.define("this",Class, Symbol.KIND.ARG);
        }

        String type = "";
        //'void' or type
        JackTokenizer.Advance();
        if (JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.VOID)
        {
            type = "void";
        }
        else 
        {
            JackTokenizer.Pointer();
            type = CompilerParse();
        }

        //subroutineName which is a identifier
        JackTokenizer.Advance();
        if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
        {
            error("subroutineName");
        }
        Subroutine = JackTokenizer.Identifier();

        //'('
        Symbol('(');

        //parameterList
        ParameterList();

        //')'
        Symbol(')');

        //subroutineBody
        compileSubroutineMain(keyword);
        CompileSubroutine();

    }
    static void compileSubroutineMain(JackTokenizer.KEYWORD keyword)
    {
        //'{'
        Symbol('{');

        //varDec*
        VarDec();

        //write VM function declaration
        writeFunctionDec(keyword);

        //statements
        StatementCompile();

        //'}'
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
        if (JackTokenizer.token() != JackTokenizer.TYPE.KEYWORD)
        {
            error("keyword");
        }
        else 
        {
            switch (JackTokenizer.KeyWords()){
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
            //type
            type = CompilerParse();

            //varName
            JackTokenizer.Advance();
            if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
            {
                error("identifier");
            }

            SymbolTable.define(JackTokenizer.Identifier(),type, Symbol.KIND.ARG);

            //',' or ')'
            JackTokenizer.Advance();
            if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || (JackTokenizer.Symbol() != ',' && JackTokenizer.Symbol() != ')'))
            {
                error("',' or ')'");
            }

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

        //type
        String type = CompilerParse();
        while(true)
        {
            //varName
            JackTokenizer.Advance();

            if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
            {
                error("identifier");
            }
            SymbolTable.define(JackTokenizer.Identifier(),type, Symbol.KIND.VAR);

            //',' or ';'
            JackTokenizer.Advance();
            if (JackTokenizer.token() != JackTokenizer.TYPE.SYMBOL || (JackTokenizer.Symbol() != ',' && JackTokenizer.Symbol() != ';'))
            {
                error("',' or ';'");
            }
            if (JackTokenizer.Symbol() == ';')
            {
                break;
            }
        }
        VarDec();
    }
    static void Do()
    {
        //subroutineCall
        SubroutineCall();

        //';'
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

        //'('
        Symbol('(');

        //expression while condition: true or false
        Expression();

        //')'
        Symbol(')');

        //if ~(condition) go to continue label
        VMWriter.writeArithmetic(VMWriter.COMMAND.NOT);
        VMWriter.writeIf(continueLabel);

        //'{'
        Symbol('{');

        //statements
        StatementCompile();

        //'}'
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
            //';'
            Symbol(';');
        }
        VMWriter.writeReturn();
    }
    static void If()
    {
        String elseLabel = Label();
        String endLabel = Label();

        //'('
        Symbol('(');

        //expression
        Expression();

        //')'
        Symbol(')');

        //if ~(condition) go to else label
        VMWriter.writeArithmetic(VMWriter.COMMAND.NOT);
        VMWriter.writeIf(elseLabel);

        //'{'
        Symbol('{');

        //statements
        StatementCompile();

        //'}'
        Symbol('}');

        //if condition after statement finishing, go to end label
        VMWriter.writeGoto(endLabel);

        VMWriter.writeLabel(elseLabel);

        //check if there is 'else'
        JackTokenizer.Advance();
        if (JackTokenizer.token() == JackTokenizer.TYPE.KEYWORD && JackTokenizer.KeyWords() == JackTokenizer.KEYWORD.ELSE)
        {
            //'{'
            Symbol('{');
            //statements
            StatementCompile();
            //'}'
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

                //']'
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
                JackTokenizer.Pointer();JackTokenizer.Pointer();
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

                //')'
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
        if (JackTokenizer.token() != JackTokenizer.TYPE.IDENTIFIER)
        {
            error("identifier");
        }
        String name = JackTokenizer.Identifier();


        JackTokenizer.Advance();
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

            //'('
            Symbol('(');

            //expressionList
            nArgs += ExpressionList();

            //')'
            Symbol(')');

            //call subroutine
            VMWriter.writeCall(name,nArgs);
        }
        else 
        {
            error("'('|'.'");
        }
    }
    static void Expression()
    {
        //term
        Term();
        
        //(op term)*
        while (true)
        {
            JackTokenizer.Advance();
            //op
            if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.IsOperation())
            {
                String opCmd = "";
                switch (JackTokenizer.Symbol())
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
                VMWriter.writeCommand(opCmd,"","");
            }
            else 
            {
                JackTokenizer.Pointer();
                break;
            }
        }
    }
    static int ExpressionList()
    {
        JackTokenizer.Advance();
        System.out.println(JackTokenizer.currentTokenType);
        System.out.println(JackTokenizer.token());
        //determine if there is any expression, if next is ')' then no
        if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == ')')
        {
            JackTokenizer.Pointer();
        }
        else 
        {
            nArgs = 1;
            JackTokenizer.Pointer();

            //expression
            Expression();
            
            //(','expression)*
            while (true) 
            {
                JackTokenizer.Advance();
                if (JackTokenizer.token() == JackTokenizer.TYPE.SYMBOL && JackTokenizer.Symbol() == ',')
                {
                    //expression
                    Expression();
                    nArgs++;
                }
                else 
                {
                    JackTokenizer.Pointer();
                    break;
                }
            }
        }
        return nArgs;
    }
    static void error(String val)
    {
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