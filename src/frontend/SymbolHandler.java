package frontend;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import Symbol.FuncType;
import Symbol.Symbol;
import Symbol.SymbolTable;
import Symbol.VarType;
import error.Error;
import node.CompUnitNode;
import token.Token;
import token.TokenType;

public class SymbolHandler {
    private static final SymbolHandler instance = new SymbolHandler();
    private SymbolHandler() {}
    public static SymbolHandler getInstance() {
        return instance;
    }

    private int scopeNum = 1;
    private SymbolTable rootSymbolTable;
    private SymbolTable curSymbolTable;

    private CompUnitNode compUnitNode;
    private ArrayList<Error> errorsList;

    private final static Map<TokenType, VarType> TokenVarMap = new HashMap<>() {{
        put(TokenType.CHARTK, VarType.Char);
        put(TokenType.INTTK, VarType.Int);
    }};

    private final static Map<TokenType, FuncType> TokenFuncMap = new HashMap<>() {{
        put(TokenType.CHARTK, FuncType.Char);
        put(TokenType.INTTK, FuncType.Int);
        put(TokenType.VOIDTK, FuncType.Void);
    }};

    public static VarType getVarType(Token token) {
        return TokenVarMap.get(token.getType());
    }

    public static FuncType getFuncType(Token token) {
        return TokenFuncMap.get(token.getType());
    }

    public void addSymbol(Symbol symbol) { 
        
        curSymbolTable.addSymbol(symbol);
        symbol.setSymbolTable(curSymbolTable);
    }

    public SymbolTable getCurSymbolTable() {
        return curSymbolTable;
    }

    public void setCurSymbolTable(SymbolTable symbolTable) {
        this.curSymbolTable = symbolTable;
    }

    public int getScopeNum() {
        return scopeNum++;
    }

    private void init() {
        Parser parser = Parser.getInstance();
        scopeNum = 1;
        compUnitNode = parser.compUnitNode;
        errorsList = parser.errorsList;
        rootSymbolTable = new SymbolTable(null);
        curSymbolTable = rootSymbolTable;
    }

    public void analyse() {
        init();
        compUnitNode.setupSymbolTable();
    }

    public void print() {
        rootSymbolTable.print();
    }
}
