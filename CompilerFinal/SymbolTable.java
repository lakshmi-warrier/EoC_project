import java.util.*;
public class SymbolTable 
{
    static HashMap<String,Symbol> classSymbols;//for STATIC, FIELD
    static HashMap<String,Symbol> subroutineSymbols;//for ARG, VAR
    static HashMap<Symbol.KIND,Integer> indices;

    public SymbolTable() 
    {
        //for STATIC, FIELD
        //scope of these variables would be class
        classSymbols = new HashMap<String, Symbol>();

        //for ARG, VAR
        //scope of these variables would be a subroutine
        subroutineSymbols = new HashMap<String, Symbol>();

        indices = new HashMap<Symbol.KIND, Integer>();

        indices.put(Symbol.KIND.ARG,0);
        indices.put(Symbol.KIND.FIELD,0);
        indices.put(Symbol.KIND.STATIC,0);
        indices.put(Symbol.KIND.VAR,0);
    }
    static void startSubroutine()
    {
        subroutineSymbols.clear();
        indices.put(Symbol.KIND.VAR,0);
        indices.put(Symbol.KIND.ARG,0);
    }
    static void define(String name, String type, Symbol.KIND kind)
    {
        // if the type is arg | var
        if (kind == Symbol.KIND.ARG || kind == Symbol.KIND.VAR)
        {
            int index = indices.get(kind);
            Symbol symbol = new Symbol(type,kind,index);

            // the arg|var are being stored into hashamp named indices with an index number
            indices.put(kind,index+1); 

            //the identifier and the symbol are being stored into the hashamp named subroutine symbol
            subroutineSymbols.put(name,symbol);
        }

        // if the type is static|field
        else if(kind == Symbol.KIND.STATIC || kind == Symbol.KIND.FIELD)
        {
            int index = indices.get(kind);
            Symbol symbol = new Symbol(type,kind,index);

            // the static|field are being stored into hashmap named indices with an index number
            indices.put(kind,index+1);

            //the identifier and the symbol are being stored into the hashamp named classsymbols symbol
            classSymbols.put(name,symbol);
        }
    }
    static int varCount(Symbol.KIND kind)
    {
        return indices.get(kind);
    }
    static Symbol.KIND kindOf(String name)
    {
        Symbol symbol = lookUp(name);

        if (symbol != null) 
        {
            return symbol.getKind();
        }
        return Symbol.KIND.NONE;
    }

    static String typeOf(String name)
    {
        Symbol symbol = lookUp(name);

        if (symbol != null) 
        {
            return symbol.getType();
        }
        return "";
    }
    static int indexOf(String name)
    {
        Symbol symbol = lookUp(name);//to take the token name
        
        if (symbol != null)
        {
            return symbol.getIndex();
        }
        return -1;
    }
    static Symbol lookUp(String name)
    {
        if (classSymbols.get(name) != null)
        {
            //if the variable is a class variable
            return classSymbols.get(name);
        }
        else if (subroutineSymbols.get(name) != null)
        {
            //if the variable's scope is current subroutine
            return subroutineSymbols.get(name);
        }
        else 
        {
            return null;
        }
    }
}
