package Symbol;

import java.util.ArrayList;
import java.util.List;

import frontend.SymbolHandler;

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
        // System.out.println("i am " + toString() + " i have son:");
        // for (SymbolTable symbolTable : sonSymbolTablesList) {
        //     System.out.println(symbolTable.scopeNum);
        // }
        // System.out.println("i have symbol:");
        // for (Symbol symbol : symbolsList) {
        //     symbol.print();
        // }
        for (Symbol symbol : symbolsList) {
            System.out.println(toString() + " " + symbol.toString());
        }
        for (SymbolTable symbolTable : sonSymbolTablesList) {
            symbolTable.print();
        }

    }

    @Override
    public String toString() {
        return scopeNum + "";
    }
}
