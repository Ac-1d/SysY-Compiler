package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import error.Error;
import error.ErrorType;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class PrimaryExpNode {//finish
    // PrimaryExp → '(' Exp ')' | LVal | Number | Character

    Token lparentToken;
    ExpNode expNode;
    Token rparentToken;
    LValNode lValNode;
    NumberNode numberNode;
    CharacterNode characterNode;
    int state;
    ExpInfo expInfo = new ExpInfo();

    public static PrimaryExpNode primaryExp() {
        Parser instance = Parser.getInstance();
        PrimaryExpNode primaryExpNode = new PrimaryExpNode();
        Token lparentToken;
        ExpNode expNode;
        Token rparentToken;
        LValNode lValNode;
        NumberNode numberNode;
        CharacterNode characterNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        lparentToken = instance.peekNextToken();
        if(lparentToken.getType().equals(TokenType.LPARENT) == true) {
            primaryExpNode.lparentToken.setLineNum(lparentToken.getLineNum());
            expNode = ExpNode.Exp();
            if(expNode == null) {
                instance.setPeekIndex(tmpIndex);
                return null;
            }
            primaryExpNode.expNode = expNode;
            tmpIndex = instance.getPeekIndex();
            rparentToken = instance.peekNextToken();
            if(rparentToken.getType().equals(TokenType.RPARENT) == false) {
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(rparentToken), ErrorType.j));
            }
            primaryExpNode.rparentToken.setLineNum(instance.getPreTokenLineNum(rparentToken));
            primaryExpNode.state = 1;
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        lValNode = LValNode.LVal();
        if(lValNode != null) {
            primaryExpNode.lValNode = lValNode;
            primaryExpNode.state = 2;
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        numberNode = NumberNode.Number();
        if(numberNode != null) {
            primaryExpNode.numberNode = numberNode;
            primaryExpNode.state = 3;
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        characterNode = CharacterNode.Character();
        if(characterNode != null) {
            primaryExpNode.characterNode = characterNode;
            primaryExpNode.state = 4;
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        return null;
    }

    void print() {
        switch (state) {
            case 1:
                lparentToken.print();
                expNode.print();
                rparentToken.print();
                break;
            case 2:
                lValNode.print();
                break;
            case 3:
                numberNode.print();
                break;
            case 4:
                characterNode.print();
                break;
            default:
                break;
        }
        System.out.println(toString());
    }

    void makeLLVM() {
        LLVMGenerator instance = LLVMGenerator.getInstance();
        
        switch (state) {
            case 1:
                expNode.makeLLVM();
                expInfo = expNode.expInfo;
                break;
            case 2:
                lValNode.makeLLVM();
                expInfo = lValNode.expInfo;
                if (lValNode.arrayNode == null) {
                    if (expInfo.isArray == false) {
                        expInfo.setReg(instance.makeLoadStmt(expInfo));
                    } else if (expInfo.isGlobal == true) {
                        expInfo.setReg(instance.makeGetelementptrStmt(expInfo));
                    }
                } else {
                    expInfo.setReg(instance.makeLoadStmt(expInfo, lValNode.arrayNode.expNode.expInfo));
                }
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    int calculateConstExp(boolean isConst) throws ExpNotConstException {
        switch (state) {
            case 1:
                return expNode.calculateConstExp(isConst);
            case 2:
                lValNode.makeLLVM();
                if (LLVMGenerator.getInstance().isDeclVarGlobal() || isConst) {
                    if (lValNode.arrayNode != null) {
                        return lValNode.varSymbol.getValue(lValNode.arrayNode.expNode.expInfo.getValue());
                    } else {
                        return lValNode.varSymbol.getValue();
                    }
                } else {
                    throw new ExpNotConstException();
                }
            case 3:
                return numberNode.getValue();
            case 4:
                return characterNode.getValue();
            default:
                throw new ExpNotConstException();
        }
    }

    @Override
    public String toString() {
        return "<PrimaryExp>";
    }

    private PrimaryExpNode() {
        lparentToken = new Token(TokenType.LPARENT, "(");
        rparentToken = new Token(TokenType.RPARENT, ")");
    }
}
