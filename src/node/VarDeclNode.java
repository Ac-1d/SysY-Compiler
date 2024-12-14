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

public class VarDeclNode {//finish
    // VarDecl → BType VarDef { ',' VarDef } ';'
    // BType → 'int' | 'char'
    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal

    BTypeNode bTypeNode;
    VarDefNode varDefNode;
    ArrayList<MultifyVarDefNode> multifyVarDefNodesList = new ArrayList<>();
    Token semicnToken;

    public static VarDeclNode VarDecl() {
        Parser instance = Parser.getInstance();
        VarDeclNode varDeclNode = new VarDeclNode();
        BTypeNode bTypeNode;
        VarDefNode varDefNode;
        MultifyVarDefNode multifyVarDefNode;
        Token semicnToken;
        int tmpIndex;
        bTypeNode = BTypeNode.BType();
        if(bTypeNode == null) {
            return null;
        }
        varDeclNode.bTypeNode = bTypeNode;
        varDefNode = VarDefNode.VarDef();
        if(varDefNode == null) {
            return null;
        }
        varDeclNode.varDefNode = varDefNode;
        tmpIndex = instance.getPeekIndex();
        while((multifyVarDefNode = MultifyVarDefNode.MultifyVarDef()) != null) {
            tmpIndex = instance.getPeekIndex();
            varDeclNode.multifyVarDefNodesList.add(multifyVarDefNode);
        }
        instance.setPeekIndex(tmpIndex);
        tmpIndex = instance.getPeekIndex();
        semicnToken = instance.peekNextToken();
        //此处不应出现左小括号^^，出现左小括号将视为正在定义函数
        if(semicnToken.getType().equals(TokenType.LPARENT) == true) {
            return null;
        }
        if(semicnToken.getType().equals(TokenType.SEMICN) == false) {
            instance.setPeekIndex(tmpIndex);
            instance.errorsList.add(new Error(instance.getPreTokenLineNum(semicnToken), ErrorType.i));
        }
        varDeclNode.semicnToken.setLineNum(instance.getPreTokenLineNum(semicnToken));
        return varDeclNode;
    }

    void makeLLVM() {
        SymbolHandler instance = SymbolHandler.getInstance();
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        llvmGenerator.setVarType(bTypeNode.intOrCharToken);
        VarType varType = SymbolHandler.getVarType(bTypeNode.intOrCharToken);
        Token identToken = varDefNode.identToken;
        boolean isConst = false;
        boolean isArray;
        isArray = varDefNode.defArrayNode != null;
        // solve 'VarDef'
        VarSymbol varSymbol = new VarSymbol(identToken, varType, isConst, isArray);
        instance.addSymbol(varSymbol);
        varDefNode.varSymbol = varSymbol;
        varDefNode.makeLLVM();
        varSymbol.setReg(varDefNode.expInfo.regIndex);
        varSymbol.setLength(varDefNode.expInfo.length);
        // solve '{ ',' VarDef }'
        for (MultifyVarDefNode multifyVarDefNode : multifyVarDefNodesList) {
            identToken = multifyVarDefNode.varDefNode.identToken;
            isArray = multifyVarDefNode.varDefNode.defArrayNode != null;
            varSymbol = new VarSymbol(identToken, varType, isConst, isArray);
            instance.addSymbol(varSymbol);
            multifyVarDefNode.varDefNode.varSymbol = varSymbol;
            multifyVarDefNode.varDefNode.makeLLVM();
            varSymbol.setReg(multifyVarDefNode.varDefNode.expInfo.regIndex);
            varSymbol.setLength(multifyVarDefNode.varDefNode.expInfo.length);
        }
    }

    void print() {
        bTypeNode.print();
        varDefNode.print();
        for (MultifyVarDefNode multifyVarDefNode : multifyVarDefNodesList) {
            multifyVarDefNode.commaToken.print();
            multifyVarDefNode.varDefNode.print();
        }
        semicnToken.print();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<VarDecl>";
    }

    private VarDeclNode() {
        semicnToken = new Token(TokenType.SEMICN, ";");
    }

    class MultifyVarDefNode {
        // MultifyVarDefNode → ',' VarDef

        Token commaToken;
        VarDefNode varDefNode;

        public static MultifyVarDefNode MultifyVarDef() {
            Parser instance = Parser.getInstance();
            MultifyVarDefNode multifyVarDefNode = (new VarDeclNode()).new MultifyVarDefNode();
            Token commaToken;
            VarDefNode varDefNode;
            commaToken = instance.peekNextToken();
            if(commaToken.getType().equals(TokenType.COMMA) == false) {
                return null;
            }
            multifyVarDefNode.commaToken.setLineNum(commaToken.getLineNum());
            varDefNode = VarDefNode.VarDef();
            if(varDefNode == null) {
                return null;
            }
            multifyVarDefNode.varDefNode = varDefNode;
            return multifyVarDefNode;
        }

        private MultifyVarDefNode() {
            commaToken = new Token(TokenType.COMMA, ",");
        }
    }
}
