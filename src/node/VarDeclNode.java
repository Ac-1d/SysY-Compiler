package node;

import token.Token;
import token.TokenType;

import java.util.ArrayList;

import error.Error;
import frontend.Parser;

public class VarDeclNode {//finish
    //VarDecl → BType VarDef { ',' VarDef } ';'

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
        semicnToken = instance.peekNextToken();
        if(semicnToken.getType().equals(TokenType.SEMICN) == false) {
            instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(semicnToken), 'i'));
        }
        varDeclNode.semicnToken.setLineNum(instance.getPreTokenLineNum(semicnToken));
        return varDeclNode;
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
        return "<VarDeclNode>";
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
