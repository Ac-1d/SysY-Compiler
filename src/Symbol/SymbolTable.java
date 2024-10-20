package Symbol;

import frontend.SymbolHandler;
import java.util.ArrayList;
import java.util.List;
import token.Token;

public class SymbolTable {
    private int scopeNum;
    private SymbolTable fatherSymbolTable;
    private List<SymbolTable> sonSymbolTablesList = new ArrayList<>();
    private List<Symbol> symbolsList = new ArrayList<>();

    public SymbolTable(SymbolTable fatherSymbolTable) {
        SymbolHandler instance = SymbolHandler.getInstance();
        this.scopeNum = instance.getScopeNum();
        if(fatherSymbolTable != null) {
            this.fatherSymbolTable = fatherSymbolTable;
            fatherSymbolTable.sonSymbolTablesList.add(this);
        }
    }

    public void addSymbol(Symbol symbol) {
        symbolsList.add(symbol);
    }

    public SymbolTable getFatherSymbolTable() {
        return fatherSymbolTable;
    }

    public void print() {
        for (Symbol symbol : symbolsList) {
            System.out.println(toString() + " " + symbol.toString());
        }
        for (SymbolTable symbolTable : sonSymbolTablesList) {
            symbolTable.print();
        }

    }

    public Symbol findSymbol(Token token) {
        for (Symbol sonSymbol : symbolsList) {
            if(token.getWord().equals(sonSymbol.word) == true) {
                return sonSymbol;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return scopeNum + "";
    }
}
