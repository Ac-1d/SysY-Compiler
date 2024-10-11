package node;

import frontend.Parser;
import token.Token;
import token.TokenType;
import error.Error;

import java.util.ArrayList;

public class ConstDeclNode {//finish
    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
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
        constDeclNode.semicnToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.SEMICN) == false) {
            //错误处理 && 回溯
            instance.setPeekIndex(tmpIndex);
            instance.errorsList.add(new Error("Parse", instance.getPreTokenLineNum(token), 'i'));
        }
        return constDeclNode;
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
