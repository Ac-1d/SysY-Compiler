package node;

import frontend.Parse;
import token.Token;

public class BTypeNode {
    //BType → 'int' | 'char'
    Token token;
    Parse instance = Parse.getInstance();

    public static BTypeNode BType() {
        BTypeNode bTypeNode = new BTypeNode();
        if(check()){
            bTypeNode.token = bTypeNode.instance.getNextToken();
        }
        else {//不会出现的错误

        }

        return bTypeNode;
    }

    private static boolean check() {
        return true;
    }

    public void print() {
        System.out.println(token.toString());
    }
    
}
