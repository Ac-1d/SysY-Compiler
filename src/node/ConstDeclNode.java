package node;

import Symbol.VarSymbol;
import Symbol.VarType;
import error.Error;
import error.ErrorType;
import frontend.LLVMGenerator;
import frontend.Parser;
import frontend.SymbolHandler;
import java.util.ArrayList;
import token.Token;
import token.TokenType;

public class ConstDeclNode {//finish
    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    // BType → 'int' | 'char'
    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal

    Token constToken;
    BTypeNode bTypeNode;
    ConstDefNode constDefNode;
    ArrayList<ConstDeclNode.MultipleDeclNode> multipleDeclNodesList = new ArrayList<>();
    Token semicnToken;

    public static ConstDeclNode constDecl() {
        Parser instance = Parser.getInstance();
        ConstDeclNode constDeclNode = new ConstDeclNode();
        BTypeNode bTypeNode;
        ConstDefNode constDefNode;
        ConstDeclNode.MultipleDeclNode multipleDeclNode;
        Token token;
        int tmpIndex;
        token = instance.peekNextToken();
        constDeclNode.constToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.CONSTTK) == false){
            return null;
        }
        bTypeNode = BTypeNode.BType();
        constDeclNode.bTypeNode = bTypeNode;
        if(bTypeNode == null) {
            return null;
        }
        constDefNode = ConstDefNode.ConstDef();
        constDeclNode.constDefNode = constDefNode;
        if(constDefNode == null) {
            return null;
        }
        //保证在最后一次识别前拿到了这个tmpIndex，在识别错误后回溯
        do {
            tmpIndex = instance.getPeekIndex();
            multipleDeclNode = MultipleDeclNode.MultipleDecl();
            if(multipleDeclNode == null) {
                instance.setPeekIndex(tmpIndex);
                break;
            }
            constDeclNode.multipleDeclNodesList.add(multipleDeclNode);
        } while (true);
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.SEMICN) == false) {
            //错误处理 && 回溯
            instance.setPeekIndex(tmpIndex);
            instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));
            constDeclNode.semicnToken.setLineNum(instance.getPreTokenLineNum(token));
        }
        else {
            constDeclNode.semicnToken = token;
        }
        return constDeclNode;
    }

    void makeLLVM() {
        SymbolHandler instance = SymbolHandler.getInstance();
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        llvmGenerator.setVarType(bTypeNode.intOrCharToken);
        VarType varType = SymbolHandler.getVarType(bTypeNode.intOrCharToken);
        Token identToken = constDefNode.identToken;
        boolean isConst = true;
        boolean isArray;
        isArray = constDefNode.defArrayNode != null;
        //solve 'ConstDef'
        VarSymbol varSymbol = new VarSymbol(identToken, varType, isConst, isArray);
        instance.addSymbol(varSymbol);
        constDefNode.varSymbol = varSymbol;
        constDefNode.makeLLVM();
        varSymbol.setReg(constDefNode.expInfo.regIndex);
        varSymbol.setLength(constDefNode.expInfo.length);
        //solve '{, ConstDef}'
        for (MultipleDeclNode multipleDeclNode : multipleDeclNodesList) {
            identToken = multipleDeclNode.constDefNode.identToken;
            isArray = multipleDeclNode.constDefNode.defArrayNode != null;
            varSymbol = new VarSymbol(identToken, varType, isConst, isArray);
            instance.addSymbol(varSymbol);
            multipleDeclNode.constDefNode.varSymbol = varSymbol;
            multipleDeclNode.constDefNode.makeLLVM();
            varSymbol.setReg(multipleDeclNode.constDefNode.expInfo.regIndex);
            varSymbol.setLength(multipleDeclNode.constDefNode.expInfo.length);
        }

    }

    void print() {
        constToken.print();
        bTypeNode.print();
        constDefNode.print();
        for (MultipleDeclNode multipleDeclNode : multipleDeclNodesList) {
            multipleDeclNode.commaToken.print();
            multipleDeclNode.constDefNode.print();
        }
        semicnToken.print();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<ConstDecl>";
    }

    private ConstDeclNode() {
        constToken = new Token(TokenType.CONSTTK, "const");
        semicnToken = new Token(TokenType.SEMICN, ";");
    }

    class MultipleDeclNode {
        //MultipleDecl → ',' ConstDef
        Token commaToken;
        ConstDefNode constDefNode;

        public static MultipleDeclNode MultipleDecl() {
            Parser instance = Parser.getInstance();
            MultipleDeclNode multipleDeclNode = (new ConstDeclNode()).new MultipleDeclNode();
            ConstDefNode constDefNode;
            Token token;
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.COMMA) == false) {
                return null;
            }
            multipleDeclNode.commaToken = token;
            constDefNode = ConstDefNode.ConstDef();
            multipleDeclNode.constDefNode = constDefNode;
            if(constDefNode == null) {
                return null;
            }
            return multipleDeclNode;
        }

        private MultipleDeclNode() {
            commaToken = new Token(TokenType.COMMA, ",");
        }
    }
    
}
