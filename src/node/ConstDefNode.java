package node;

import Symbol.ExpInfo;
import Symbol.VarSymbol;
import error.Error;
import error.ErrorType;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class ConstDefNode {//finish
    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    // 此处不检查数组/变量声明与数组/变量初始化是否匹配
    Token identToken;
    /** DefArrayNode → '[' ConstExp ']' */
    DefArrayNode defArrayNode;
    Token assignToken;
    ConstInitValNode constInitValNode;
    VarSymbol varSymbol;
    ExpInfo expInfo = new ExpInfo();
    
    public static ConstDefNode ConstDef() {
        Parser instance = Parser.getInstance();
        ConstDefNode constDefNode = new ConstDefNode();
        ConstDefNode.DefArrayNode defArrayNode;
        ConstInitValNode constInitValNode;
        Token token;
        int tmpIndex;
        token = instance.peekNextToken();
        constDefNode.identToken = token;
        if(token.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        tmpIndex = instance.getPeekIndex();
        defArrayNode = ConstDefNode.DefArrayNode.DefArray();
        constDefNode.defArrayNode = defArrayNode;
        if(defArrayNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        token = instance.peekNextToken();
        constDefNode.assignToken = token;
        if(token.getType().equals(TokenType.ASSIGN) == false) {
            return null;
        }
        constInitValNode = ConstInitValNode.ConstInitVal();
        constDefNode.constInitValNode = constInitValNode;
        if(constInitValNode == null) {
            return null;
        }
        return constDefNode;
    }

    void print() {
        identToken.print();
        if(defArrayNode != null) {
            defArrayNode.lbrackToken.print();
            defArrayNode.constExpNode.print();
            defArrayNode.rbrackToken.print();
        }
        assignToken.print();
        constInitValNode.print();
        System.out.println(toString());
    }

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        if (defArrayNode != null) {
            // defArrayNode.constExpNode.setupSymbolTable();
            defArrayNode.constExpValue = defArrayNode.constExpNode.calculateConstExp();
        }
        constInitValNode.makeLLVM();
        int reg;
        switch (constInitValNode.state) {
            case 1:
                reg = llvmGenerator.makeDeclStmt(identToken.getWord(), constInitValNode.constExpValue);
                expInfo = new ExpInfo(null, reg);
                break;
            case 2:
                expInfo.setReg(llvmGenerator.makeArrayDeclStmt(identToken.getWord(), defArrayNode.constExpValue, constInitValNode.expInfos));
                expInfo.length = defArrayNode.constExpValue;
                break;
            case 3:
                String str = constInitValNode.strconToken.getWord();
                expInfo.setReg(llvmGenerator.makeStrDeclStmt(str, identToken.getWord(), defArrayNode.constExpValue));
                expInfo.length = defArrayNode.constExpValue;
                break;
        }
    }

    @Override
    public String toString() {
        return "<ConstDef>";
    }

    private ConstDefNode() {}

    /** DefArrayNode → '[' ConstExp ']' */
    class DefArrayNode {//finish
        Token lbrackToken;
        ConstExpNode constExpNode;
        Token rbrackToken;
        int constExpValue;

        public static DefArrayNode DefArray() {
            Parser instance = Parser.getInstance();
            DefArrayNode defArrayNode = (new ConstDefNode()).new DefArrayNode();
            ConstExpNode constExpNode;
            Token token;
            int tmpIndex;
            token = instance.peekNextToken();
            defArrayNode.lbrackToken = token;
            if(token.getType().equals(TokenType.LBRACK) == false) {
                return null;
            }
            constExpNode = ConstExpNode.ConstExp();
            defArrayNode.constExpNode = constExpNode;
            if(constExpNode == null) {
                return null;
            }
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.RBRACK) == false) {//未识别到']'
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.k));
                instance.setPeekIndex(tmpIndex);
                defArrayNode.rbrackToken.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                defArrayNode.rbrackToken.setLineNum(token.getLineNum());
            }
            return defArrayNode;
        }

        private DefArrayNode() {
            rbrackToken = new Token(TokenType.RBRACK, "]");
        }

    }
}
