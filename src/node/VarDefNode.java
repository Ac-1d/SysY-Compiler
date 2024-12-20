package node;

import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.List;

import Symbol.ExpInfo;
import Symbol.VarSymbol;
import error.Error;
import error.ErrorType;

public class VarDefNode {//finish
    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal

    Token identToken;
    DefArrayNode defArrayNode;
    Token assignToken;
    InitValNode initValNode;
    VarSymbol varSymbol;
    ExpInfo expInfo = new ExpInfo();

    public static VarDefNode VarDef() {
        Parser instance = Parser.getInstance();
        VarDefNode varDefNode = new VarDefNode();
        Token indentToken;
        DefArrayNode defArrayNode;
        Token assignToken;
        InitValNode initValNode;
        int tmpIndex;
        indentToken = instance.peekNextToken();
        if(indentToken.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        varDefNode.identToken = indentToken;
        tmpIndex = instance.getPeekIndex();
        defArrayNode = DefArrayNode.DefArray();
        if(defArrayNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        varDefNode.defArrayNode = defArrayNode;
        tmpIndex = instance.getPeekIndex();
        assignToken = instance.peekNextToken();
        if(assignToken.getType().equals(TokenType.ASSIGN) == false) {
            instance.setPeekIndex(tmpIndex);
            return varDefNode;
        }
        varDefNode.assignToken.setLineNum(assignToken.getLineNum());
        initValNode = InitValNode.InitVal();
        if(initValNode == null) {
            return null;
        }
        varDefNode.initValNode = initValNode;
        return varDefNode;
    }

    void print() {
        identToken.print();
        if(defArrayNode != null) {
            defArrayNode.lbrackToken.print();
            defArrayNode.constExpNode.print();
            defArrayNode.rbrackToken.print();
        }
        if(initValNode != null) {
            assignToken.print();
            initValNode.print();
        }
        System.out.println(toString());
    }

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        if (defArrayNode != null) {
            // defArrayNode.constExpNode.setupSymbolTable();
            defArrayNode.constExpValue = defArrayNode.constExpNode.calculateConstExp();
            varSymbol.setLength(defArrayNode.constExpValue);
        }
        if (initValNode != null) {
            initValNode.makeLLVM();
            expInfo = initValNode.expInfo;
        }
        if (initValNode == null) {//无初始化
            varSymbol.setValue();
            if (defArrayNode == null) {
                expInfo.regIndex = llvmGenerator.makeDeclStmt(identToken.getWord(), null);
            } else {// case 2
                expInfo.setReg(llvmGenerator.makeArrayDeclStmt(identToken.getWord(), defArrayNode.constExpValue, new ArrayList<>()));
                expInfo.length = defArrayNode.constExpValue;
            }
        } else {
            switch (initValNode.state) {
                case 1:
                    if (initValNode.expNode.expInfo.getValue() != null) {//有初始化 编译期可计算
                        if (defArrayNode == null) {
                            expInfo.regIndex = llvmGenerator.makeDeclStmt(identToken.getWord(), initValNode.expNode.expInfo.getValue());
                        } else {
                            List<ExpInfo> expInfos = new ArrayList<>();
                            expInfos.add(new ExpInfo(initValNode.expNode.expInfo.getValue()));
                            expInfo.setReg(llvmGenerator.makeArrayDeclStmt(identToken.getWord(), defArrayNode.constExpValue, expInfos));
                        }
                        varSymbol.setValue(initValNode.expNode.expInfo.getValue());
                    } else {//有初始化 编译期不可计算
                        if (defArrayNode == null) {
                            expInfo.regIndex = llvmGenerator.makeDeclStmt(expInfo);
                        } else {
                            List<ExpInfo> expInfos = new ArrayList<>();
                            expInfos.add(expInfo);
                            expInfo.setReg(llvmGenerator.makeArrayDeclStmt(identToken.getWord(), defArrayNode.constExpValue, expInfos));
                        }
                    }
                    break;
                case 2:
                    varSymbol.setValue(initValNode.expInfos);
                    expInfo.setReg(llvmGenerator.makeArrayDeclStmt(identToken.getWord(), defArrayNode.constExpValue, initValNode.expInfos));
                    expInfo.length = defArrayNode.constExpValue;
                    break;
                case 3:
                String str = initValNode.strconToken.getWord();
                varSymbol.setValue(str);
                    expInfo.setReg(llvmGenerator.makeStrDeclStmt(str, identToken.getWord(), defArrayNode.constExpValue));
                    expInfo.length = defArrayNode.constExpValue;
                    break;
                default:
                    break;
            }
        } 
        }

    @Override
    public String toString() {
        return "<VarDef>";
    }

    private VarDefNode() {
        assignToken = new Token(TokenType.ASSIGN, "=");
    }

    class DefArrayNode {//finish
        Token lbrackToken;
        ConstExpNode constExpNode;
        Token rbrackToken;
        VarSymbol varSymbol;
        int constExpValue;

        public static DefArrayNode DefArray() {
            Parser instance = Parser.getInstance();
            DefArrayNode defArrayNode = (new VarDefNode()).new DefArrayNode();
            ConstExpNode constExpNode;
            Token token;
            int tmpIndex;
            token = instance.peekNextToken();
            defArrayNode.lbrackToken.setLineNum(token.getLineNum());
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
                defArrayNode.rbrackToken.setLineNum(instance.getPreTokenLineNum(token));
                instance.setPeekIndex(tmpIndex);
            }
            else {
                defArrayNode.rbrackToken.setLineNum(token.getLineNum());
            }
            return defArrayNode;
        }

        private DefArrayNode() {
            lbrackToken = new Token(TokenType.LBRACK, "[");
            rbrackToken = new Token(TokenType.RBRACK, "]");
        }

    }

}
