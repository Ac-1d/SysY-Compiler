package node;

import token.Token;
import token.TokenType;

import java.util.ArrayList;

import error.Error;
import frontend.Parser;

public class StmtNode {
    /*
    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
        | [Exp] ';' //有无Exp两种情况
        | Block
        | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
        | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 假设这一行不分开😅 分开写就紫砂
        | 'break' ';' | 'continue' ';'
        | 'return' [Exp] ';' // 1.有Exp 2.无Exp
        | LVal '=' 'getint''('')'';'
        | LVal '=' 'getchar''('')'';'
        | 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp
    */

    LValNode lValNode;
    Token assignToken;
    /**in case 1, 2, 7 and 10 */
    ExpNode expNode;
    Token semicnToken1;
    Token semicnToken2;
    BlockNode blockNode;
    Token ifToken;
    Token lparentToken;
    Token rparentToken;
    /**in case 4 and 5 */
    CondNode condNode;
    /**for structure if and for*/
    StmtNode stmtNode1;
    /**for structure if */
    StmtNode stmtNode2;
    Token elseToken;
    Token forToken;
    ForStmtNode forStmtNode1;
    ForStmtNode forStmtNode2;
    Token breakToken;
    Token continueToken;
    Token returnToken;
    Token getintToken;
    Token getcharToken;
    Token printfToken;
    Token strconToken;
    ArrayList<ExpWithCommaNode> expWithCommaNodesList = new ArrayList<>();
    int state;

    /*
    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖 √
        | [Exp] ';' //有无Exp两种情况 √
        | Block √
        | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else √
        | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt √
        | 'break' ';' | 'continue' ';' √
        | 'return' [Exp] ';' // 1.有Exp 2.无Exp √
        | LVal '=' 'getint''('')'';' √
        | LVal '=' 'getchar''('')'';' √
        | 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp √
    */

    public static StmtNode Stmt() {
        Parser instance = Parser.getInstance();
        StmtNode stmtNode = new StmtNode();
        LValNode lValNode;
        Token token;
        ExpNode expNode;
        BlockNode blockNode;
        CondNode condNode;
        StmtNode stmtNode1;
        StmtNode stmtNode2;
        ForStmtNode forStmtNode1;
        ForStmtNode forStmtNode2;
        ExpWithCommaNode expWithCommaNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        lValNode = LValNode.LVal();
        do {
            if(lValNode != null) {//case 1, 8 or 9
                stmtNode.lValNode = lValNode;
                token = instance.peekNextToken();
                if(token.getType().equals(TokenType.ASSIGN) == false) {
                    break;
                }
                stmtNode.assignToken = token;
                int ttmpIndex = instance.getPeekIndex();
                expNode = ExpNode.Exp();
                if(expNode != null) {//case 1
                    stmtNode.expNode = expNode;
                    ttmpIndex = instance.getPeekIndex();
                    token = instance.peekNextToken();
                    if(token.getType().equals(TokenType.SEMICN) == false) {//error
                        instance.setPeekIndex(ttmpIndex);
                        instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'i'));    
                    }
                    stmtNode.semicnToken1.setLineNum(instance.getPreTokenLineNum(token));
                    stmtNode.state = 1;
                    return stmtNode;
                }
                instance.setPeekIndex(ttmpIndex);
                token = instance.peekNextToken();
                if(token.getType().equals(TokenType.GETINTTK) == true || token.getType().equals(TokenType.GETCHARTK) == true) {//case 8 or 9 they are the same😊
                    if(token.getType().equals(TokenType.GETINTTK) == true) {
                        stmtNode.state = 8;
                        stmtNode.getintToken = token;
                    }
                    else {
                        stmtNode.state = 9;
                        stmtNode.getcharToken = token;
                    }
                    token = instance.peekNextToken();
                    if(token.getType().equals(TokenType.LPARENT) == false) {
                        break;
                    }
                    stmtNode.lparentToken = token;
                    ttmpIndex = instance.getPeekIndex();
                    token = instance.peekNextToken();
                    if(token.getType().equals(TokenType.RPARENT) == false) {//error
                        instance.setPeekIndex(ttmpIndex);
                        instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'j'));
                    }
                    stmtNode.rparentToken.setLineNum(instance.getPreTokenLineNum(token));
                    ttmpIndex = instance.getPeekIndex();
                    token = instance.peekNextToken();
                    if(token.getType().equals(TokenType.SEMICN) == false) {
                        instance.setPeekIndex(ttmpIndex);
                        instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'i'));
                        stmtNode.semicnToken1.setLineNum(instance.getPreTokenLineNum(token));
                    }
                    else {
                        stmtNode.semicnToken1 = token;
                    }
                    return stmtNode;
                }
                break;
            }
            break;
        } while (true);
        instance.setPeekIndex(tmpIndex);
        expNode = ExpNode.Exp();
        if(expNode != null) {//case 2
            stmtNode.expNode = expNode;
            int ttmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {
                instance.setPeekIndex(ttmpIndex);
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'i'));
                stmtNode.semicnToken1.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                stmtNode.semicnToken1 = token;
            }
            stmtNode.state = 2;
            return stmtNode;
        }
        instance.setPeekIndex(tmpIndex);
        blockNode = BlockNode.Block();
        if(blockNode != null) {//case 3
            stmtNode.blockNode = blockNode;
            stmtNode.state = 3;
            return stmtNode;
        }
        instance.setPeekIndex(tmpIndex);
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.IFTK) == true){//case 4
            stmtNode.ifToken = token;
            stmtNode.state = 4;
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.LPARENT) == false) {
                return null;
            }
            stmtNode.lparentToken = token;
            condNode = CondNode.Cond();
            if(condNode == null) {
                return null;
            }
            stmtNode.condNode = condNode;
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.RPARENT) == false) {//error
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'j'));
            }
            stmtNode.rparentToken.setLineNum(instance.getPreTokenLineNum(token));
            stmtNode1 = StmtNode.Stmt();
            if(stmtNode1 == null) {
                return null;
            }
            stmtNode.stmtNode1 = stmtNode1;
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.ELSETK) == false) {
                instance.setPeekIndex(tmpIndex);
                return stmtNode;
            }
            stmtNode.elseToken = token;
            stmtNode2 = StmtNode.Stmt();
            if(stmtNode2 == null) {
                instance.setPeekIndex(tmpIndex);
                return stmtNode;
            }
            stmtNode.stmtNode2 = stmtNode2;
            return stmtNode;
        }
        //上一个if一定会return 所以这里的token仍然是我们期望的token
        if(token.getType().equals(TokenType.FORTK) == true) {//case 5
            stmtNode.forToken = token;
            stmtNode.state = 5;
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.LPARENT) == false) {
                return null;
            }
            stmtNode.lparentToken = token;
            tmpIndex = instance.getPeekIndex();
            forStmtNode1 = ForStmtNode.ForStmt();
            if(forStmtNode1 == null) {
                instance.setPeekIndex(tmpIndex);
            }
            else {
                stmtNode.forStmtNode1 = forStmtNode1;
            }
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {
                return null;
            }
            stmtNode.semicnToken1 = token;
            tmpIndex = instance.getPeekIndex();
            condNode = CondNode.Cond();
            if(condNode == null) {
                instance.setPeekIndex(tmpIndex);
            }
            else {
                stmtNode.condNode = condNode;
            }
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {
                return null;
            }
            stmtNode.semicnToken2 = token;
            tmpIndex = instance.getPeekIndex();
            forStmtNode2 = ForStmtNode.ForStmt();
            if(forStmtNode2 == null) {
                instance.setPeekIndex(tmpIndex);
            }
            else {
                stmtNode.forStmtNode2 = forStmtNode2;
            }
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.RPARENT) == false) {
                return null;
            }
            stmtNode.rparentToken = token;
            stmtNode1 = StmtNode.Stmt();
            if(stmtNode1 == null) {
                return null;
            }
            stmtNode.stmtNode1 = stmtNode1;
            return stmtNode;
        }
        if(token.getType().equals(TokenType.BREAKTK) == true) {//case 6
            stmtNode.breakToken = token;
            stmtNode.state = 6;
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {// error //其实这个是正确的错误处理方式，之前的写法有一些隐患 有空改改吧
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'i'));
                stmtNode.semicnToken1.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                stmtNode.semicnToken1 = token;
            }
            return stmtNode;
        }
        if(token.getType().equals(TokenType.CONTINUETK) == true) {//case 6
            stmtNode.continueToken = token;
            stmtNode.state = 6;
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {// error
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'i'));
                stmtNode.semicnToken1.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                stmtNode.semicnToken1 = token;
            }
            return stmtNode;
        }
        if(token.getType().equals(TokenType.RETURNTK) == true) {//case 7
            stmtNode.returnToken = token;
            stmtNode.state = 7;
            tmpIndex = instance.getPeekIndex();
            expNode = ExpNode.Exp();
            if(expNode == null) {
                instance.setPeekIndex(tmpIndex);
            }
            stmtNode.expNode = expNode;
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {//error
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'i'));
                stmtNode.semicnToken1.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                stmtNode.semicnToken1 = token;
            }
            return stmtNode;
        }
        if(token.getType().equals(TokenType.PRINTFTK) == true) {//case 10
            stmtNode.printfToken = token;
            stmtNode.state = 10;
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.LPARENT) == false) {
                return null;
            }
            stmtNode.lparentToken = token;
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.STRCON) == false) {
                return null;
            }
            stmtNode.strconToken = token;
            do {//do-while结构处理更好
                tmpIndex = instance.getPeekIndex();
                expWithCommaNode = ExpWithCommaNode.ExpWithComma();
                if(expWithCommaNode == null) {
                    instance.setPeekIndex(tmpIndex);
                    break;
                }
                stmtNode.expWithCommaNodesList.add(expWithCommaNode);
            } while (true);
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.RPARENT) == false) {//error
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'j'));
                stmtNode.rparentToken.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                stmtNode.returnToken = token;
            }
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'i'));
                stmtNode.semicnToken1.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                stmtNode.semicnToken1 = token;
            }
            return stmtNode;
        }
        if(token.getType().equals(TokenType.SEMICN) == true) {//case 2
            stmtNode.semicnToken1 = token;
            stmtNode.state = 2;
            return stmtNode;
        }
        return null;
    }

    void print() {
        switch (state) {
            case 1:
                lValNode.print();
                assignToken.print();
                expNode.print();
                semicnToken1.print();
                break;
            case 2:
                if(expNode != null) {
                    expNode.print();
                }
                semicnToken1.print();
                break;
            case 3:
                blockNode.print();
                break;
            case 4:
                ifToken.print();
                lparentToken.print();
                condNode.print();
                rparentToken.print();
                stmtNode1.print();
                if(elseToken != null) {
                    elseToken.print();
                    stmtNode2.print();
                }
                break;
            case 5:
                forToken.print();
                lparentToken.print();
                if(forStmtNode1 != null) {
                    forStmtNode1.print();
                }
                semicnToken1.print();
                if(condNode != null) {
                    condNode.print();
                }
                semicnToken2.print();
                if(forStmtNode2 != null) {
                    forStmtNode2.print();
                }
                rparentToken.print();
                stmtNode1.print();
                break;
            case 6:
                if(breakToken != null) {
                    breakToken.print();
                }
                else {
                    continueToken.print();
                }
                semicnToken1.print();
                break;
            case 7:
                returnToken.print();
                if(expNode != null) {
                    expNode.print();
                }
                semicnToken1.print();
                break;
            case 8:
                lValNode.print();
                assignToken.print();
                getintToken.print();
                lparentToken.print();
                rparentToken.print();
                semicnToken1.print();
                break;
            case 9:
            lValNode.print();
            assignToken.print();
            getcharToken.print();
            lparentToken.print();
            rparentToken.print();
            semicnToken1.print();
                break;
            case 10:
                printfToken.print();
                lparentToken.print();
                strconToken.print();
                for (ExpWithCommaNode expWithCommaNode : expWithCommaNodesList) {
                    expWithCommaNode.commaToken.print();
                    expWithCommaNode.expNode.print();
                }
                rparentToken.print();
                semicnToken1.print();
                break;
            default:
                break;
        }
        System.out.println(toString());
    }

    void setupSymbolTable() {
        switch (state) {
            case 3:
                blockNode.setupSymbolTable(false);
                break;
            case 4:
                stmtNode1.setupSymbolTable();
                if(stmtNode2 != null) {
                    stmtNode2.setupSymbolTable();
                }
                break;
            case 5:
                stmtNode1.setupSymbolTable();
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "<Stmt>";
    }

    private StmtNode() {
        semicnToken1 = new Token(TokenType.SEMICN, ";");
        rparentToken = new Token(TokenType.RPARENT, ")");
    }

    class ExpWithCommaNode {
        // ExpWithCommaNode → ',' Exp

        Token commaToken;
        ExpNode expNode;

        public static ExpWithCommaNode ExpWithComma() {
            Parser instance = Parser.getInstance();
            ExpWithCommaNode expWithCommaNode = (new StmtNode()).new ExpWithCommaNode();
            Token commaToken;
            ExpNode expNode;
            commaToken = instance.peekNextToken();
            if(commaToken.getType().equals(TokenType.COMMA) == false) {
                return null;
            }
            expWithCommaNode.commaToken = commaToken;
            expNode = ExpNode.Exp();
            if(expNode == null) {
                return null;
            }
            expWithCommaNode.expNode = expNode;
            return expWithCommaNode;
        }

    }
    
}
