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
        if(instance.peekNextToken().getType().equals(TokenType.CONSTTK) == false){
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
        tmpIndex = instance.getPeekIndex();
        while((multipleDeclNode = ConstDeclNode.MultipleDeclNode.MultipleDecl()) != null) {
            constDeclNode.multipleDeclNodesList.add(multipleDeclNode);
            tmpIndex = instance.getPeekIndex();
        }
        //可以预期到，这里一定以错误的识别收尾
        instance.setPeekIndex(tmpIndex);
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
            if(instance.peekNextToken().getType().equals(TokenType.COMMA) == false) {
                return null;
            }
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
