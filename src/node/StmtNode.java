package node;

public class StmtNode {
    /*
    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
        | [Exp] ';' //有无Exp两种情况
        | Block
        | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
        | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        | 'break' ';' | 'continue' ';'
        | 'return' [Exp] ';' // 1.有Exp 2.无Exp
        | LVal '=' 'getint''('')'';'
        | LVal '=' 'getchar''('')'';'
        | 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp
    */
    
}
