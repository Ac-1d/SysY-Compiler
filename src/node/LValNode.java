package node;

import Symbol.Symbol;
import Symbol.SymbolTable;
import Symbol.VarSymbol;
import error.Error;
import error.ErrorType;
import frontend.ErrorHandler;
import frontend.Parser;
import frontend.SymbolHandler;
import token.Token;
import token.TokenType;

public class LValNode {//finish
    // LVal → Ident ['[' Exp ']'] 

    Token identToken;
    ArrayNode arrayNode;
    VarSymbol varSymbol;

    public static LValNode LVal() {
        Parser instance = Parser.getInstance();
        LValNode lValNode = new LValNode();
        Token identToken;
        ArrayNode arrayNode;
        int tmpIndex;
        identToken = instance.peekNextToken();
        if(identToken.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        lValNode.identToken = identToken;
        tmpIndex = instance.getPeekIndex();
        arrayNode = ArrayNode.Array();
        if(arrayNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        else {
            lValNode.arrayNode = arrayNode;
        }
        return lValNode;
    }

    void print() {
        identToken.print();
        if(arrayNode != null) {
            arrayNode.lbrackToken.print();
            arrayNode.expNode.print();
            arrayNode.rbrackToken.print();
        }
        System.out.println(toString());
    }

    void setupSymbolTable() {
        SymbolHandler symbolHandler = SymbolHandler.getInstance();
        ErrorHandler errorHandler = ErrorHandler.getInstance();
        SymbolTable symbolTable = symbolHandler.findSymbolTableHasIdent(identToken);
        if (symbolTable == null) {
            errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.c));
        } else {
            Symbol symbol = symbolTable.findSymbol(identToken);
            boolean isVarSymbol = symbol.getClass().equals(VarSymbol.class);
            if (isVarSymbol == false) {
                errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.c));
            } else {
                varSymbol = (VarSymbol) symbol;
            }
        }
        if (arrayNode != null) {
            arrayNode.expNode.makeLLVM();
        }
    }

    void checkIfConst() {
        if (varSymbol == null) {
            return;
        }
        if (varSymbol.isConst() == true) {
            ErrorHandler.getInstance().addError(new Error(identToken.getLineNum(), ErrorType.h));
        }
    }

    @Override
    public String toString() {
        return "<LVal>";
    }

    private LValNode() {}

    class ArrayNode {
        // ArrayNode → '[' Exp ']'

        Token lbrackToken;
        ExpNode expNode;
        Token rbrackToken;

        public static ArrayNode Array() {
            Parser instance = Parser.getInstance();
            ArrayNode arrayNode = (new LValNode()).new ArrayNode();
            Token lbrackToken;
            ExpNode expNode;
            Token rbrackToken;
            int tmpIndex;
            lbrackToken = instance.peekNextToken();
            if(lbrackToken.getType().equals(TokenType.LBRACK) == false) {
                return null;
            }
            arrayNode.lbrackToken.setLineNum(lbrackToken.getLineNum());
            expNode = ExpNode.Exp();
            if(expNode == null) {
                return null;
            }
            arrayNode.expNode = expNode;
            tmpIndex = instance.getPeekIndex();
            rbrackToken = instance.peekNextToken();
            if(rbrackToken.getType().equals(TokenType.RBRACK) == false) {
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(rbrackToken), ErrorType.k));
            }
            arrayNode.rbrackToken.setLineNum(rbrackToken.getLineNum());
            return arrayNode;
        }

        private ArrayNode() {
            lbrackToken = new Token(TokenType.LBRACK, "[");
            rbrackToken = new Token(TokenType.RBRACK, "]");
        }

    }
}
