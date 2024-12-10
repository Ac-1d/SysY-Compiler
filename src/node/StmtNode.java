package node;

import error.Error;
import error.ErrorType;
import frontend.ErrorHandler;
import frontend.LLVMGenerator;
import frontend.Parser;
import java.util.ArrayList;
import java.util.List;

import token.Token;
import token.TokenType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Symbol.ExpInfo;
import Symbol.FuncRParam;
import Symbol.FuncType;
import Symbol.VarType;

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
    List<String> strsList;
    ArrayList<Integer> strNumsList;
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
                        instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));    
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
                        instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.j));
                    }
                    stmtNode.rparentToken.setLineNum(instance.getPreTokenLineNum(token));
                    ttmpIndex = instance.getPeekIndex();
                    token = instance.peekNextToken();
                    if(token.getType().equals(TokenType.SEMICN) == false) {
                        instance.setPeekIndex(ttmpIndex);
                        instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));
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
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));
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
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.j));
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
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));
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
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));
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
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));
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
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.j));
                stmtNode.rparentToken.setLineNum(instance.getPreTokenLineNum(token));
            }
            else {
                stmtNode.returnToken = token;
            }
            tmpIndex = instance.getPeekIndex();
            token = instance.peekNextToken();
            if(token.getType().equals(TokenType.SEMICN) == false) {
                instance.setPeekIndex(tmpIndex);
                instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.i));
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

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo lValNodeExpInfo, expNodeExpInfo, funcExpInfo = new ExpInfo();
        switch (state) {
            case 1: // LVal '=' Exp ';'
                lValNode.makeLLVM();
                lValNodeExpInfo = lValNode.expInfo;
                lValNode.checkIfConst();
                expNode.makeLLVM();
                expNodeExpInfo = expNode.expInfo;
                llvmGenerator.makeStoreStmt(expNodeExpInfo, lValNodeExpInfo);
                break;
            case 2:
                if (expNode != null) {
                    expNode.makeLLVM();
                }
                break;
            case 3:
                blockNode.makeLLVM(false);
                break;
            case 4:// if
                int label1, label2;
                condNode.makeLLVM();
                llvmGenerator.makeIfStmt(condNode.expInfo);
                label1 = llvmGenerator.setLabel();
                stmtNode1.makeLLVM();
                llvmGenerator.quitIfStmt();
                if(stmtNode2 != null) {
                    label2 = llvmGenerator.setLabel();
                    stmtNode2.makeLLVM();
                    llvmGenerator.quitIfStmt();
                    llvmGenerator.setBr(label1, label2);
                    break;
                }
                llvmGenerator.setBr(label1);
                break;
            case 5:
                ErrorHandler.loopNum++;
                if (forStmtNode1 != null) {
                    forStmtNode1.setupSymbolTable();
                }
                if (condNode != null) {
                    condNode.makeLLVM(); 
                }
                if (forStmtNode2 != null) {
                    forStmtNode2.setupSymbolTable();
                }
                stmtNode1.makeLLVM();
                ErrorHandler.loopNum--;
                break;
            case 7: // 'return' [Exp] ';'
                if (expNode != null) {
                    expNode.makeLLVM();
                    llvmGenerator.makeReturnRegStmt(expNode.expInfo);
                } else {
                    llvmGenerator.makeReturnStmt();
                }
                break;
            case 8: // LVal '=' 'getint''('')'';'
                lValNode.makeLLVM();
                lValNodeExpInfo = lValNode.expInfo;
                funcExpInfo.regIndex = llvmGenerator.makeCallFunctionStmt("getint", FuncType.Int);
                funcExpInfo.varType = VarType.Int;
                llvmGenerator.makeStoreStmt(funcExpInfo, lValNodeExpInfo);
                lValNode.checkIfConst();
                break;
            case 9: // LVal '=' 'getchar''('')'';'
                lValNode.makeLLVM();
                lValNodeExpInfo = lValNode.expInfo;
                funcExpInfo.regIndex = llvmGenerator.makeCallFunctionStmt("getchar", FuncType.Int);
                funcExpInfo.varType = VarType.Int;
                llvmGenerator.makeStoreStmt(funcExpInfo, lValNodeExpInfo);
                lValNode.checkIfConst();
                break;
            case 10: // 'printf''('StringConst {','Exp}')'';'
                strsList = cutConstr(strconToken.getWord().substring(1, strconToken.getWord().length() - 1));
                checkPrint();
                break;
            default:
                break;
        }
    }

    void checkVoidFuncReturn() {
        switch (state) {
            case 3:
                for (BlockItemNode blockItemNode : blockNode.blockItemNodesList) {
                    blockItemNode.checkVoidFuncReturn();
                }
                break;
            case 4:
                stmtNode1.checkVoidFuncReturn();
                if (stmtNode2 != null) {
                    stmtNode2.checkVoidFuncReturn();
                }
                break;
            case 5:
                stmtNode1.checkVoidFuncReturn();
                break;
            case 7:
                if (expNode != null) {
                    ErrorHandler.getInstance().addError(new Error(returnToken.getLineNum(), ErrorType.f));
                }
        
            default:
                break;
        }
    }

    void checkPrint() {
        int countD = countOccurenceRegex(strconToken.getWord(), "%d");
        int countC = countOccurenceRegex(strconToken.getWord(), "%c");
        if (countC + countD != expWithCommaNodesList.size()) {
            ErrorHandler.getInstance().addError(new Error(printfToken.getLineNum(), ErrorType.l));
        }
    }

    void checkBreak() {
        if (ErrorHandler.loopNum == 0 && state == 6) {
            ErrorHandler.getInstance().addError(new Error((breakToken != null ? breakToken.getLineNum() : continueToken.getLineNum()), ErrorType.m));
        }
    }

    private int countOccurenceRegex(String str, String sub) {
        int count = 0;
        Pattern pattern = Pattern.compile(Pattern.quote(sub));
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    List<String> cutConstr(String str) {
        String regex = "(%c|%d)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        List<String> resultList = new ArrayList<>();
        int lastEnd = 0;
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        int index = 0;

        while (matcher.find()) {
            if (lastEnd != matcher.start()) {
                String cuttedString = str.substring(lastEnd, matcher.start());
                int strNum = llvmGenerator.getStrconTokenNum();
                llvmGenerator.makeConstrStmt(cuttedString, strNum, cuttedString.length() + 1);
                llvmGenerator.makeCallPutstrStmt(strNum);
            }
            ExpNode expNode = expWithCommaNodesList.get(index++).expNode;
            expNode.makeLLVM();
            ExpInfo expInfo = expNode.expInfo;
            if (expInfo.varType.equals(VarType.Char)) {//转换条件
                expInfo = llvmGenerator.makeTransStmt(expInfo);
                
            }
            FuncRParam funcRParam = new FuncRParam(expInfo, false);
            llvmGenerator.makeCallFunctionStmt(matcher.group().equals("%c") ? "putch" : "putint", FuncType.Void, funcRParam);
            lastEnd = matcher.end();
        }
        if (lastEnd != str.length()) {
            str = str.substring(lastEnd);
            int strNum = llvmGenerator.getStrconTokenNum();
            llvmGenerator.makeConstrStmt(str, strNum, str.length() + 1);
            llvmGenerator.makeCallPutstrStmt(strNum);
        }
        return resultList;
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
