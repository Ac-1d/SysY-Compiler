package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import Symbol.VarType;

public class ExpNode {//finish
    // Exp → AddExp

    AddExpNode addExpNode;
    boolean isArray;
    /**若isArray = false, 则可能为null */
    VarType varType;
    ExpInfo expInfo = new ExpInfo();

    public static ExpNode Exp() {
        ExpNode expNode = new ExpNode();
        AddExpNode addExpNode;
        addExpNode = AddExpNode.AddExp();
        if(addExpNode == null) {
            return null;
        }
        expNode.addExpNode = addExpNode;
        return expNode;
    }
    
    void print() {
        addExpNode.print();
        System.out.println(toString());
    }
    
    void makeLLVM() {
        setVarType();
        try {
            expInfo.setValue(calculateConstExp(false));
        } catch (ExpNotConstException e) {
            addExpNode.makeLLVM();
            expInfo = addExpNode.expInfo;
            
        }
    }

    //调用者需要知晓该异常的发生
    int calculateConstExp(boolean isConst) throws ExpNotConstException {
        return addExpNode.calculateConstExp(isConst);
    }

    //为判断函数的形参服务
    private void setVarType() {
        AddExpNode shorterAddExpNode = addExpNode.shorterAddExpNode;
        if (shorterAddExpNode != null) {// 1 + 2
            isArray = false;
            return;
        }
        MulExpNode shorterMulExpNode = addExpNode.mulExpNode.shorterMulExpNode;
        if (shorterMulExpNode != null) {// 1 * 2
            isArray = false;
            return;
        }
        UnaryExpNode unaryExpNode = addExpNode.mulExpNode.unaryExpNode;
        if (unaryExpNode.state == 2 || unaryExpNode.state == 3) {// func() / +-+1
            isArray = false;
            return;
        }
        PrimaryExpNode primaryExpNode = addExpNode.mulExpNode.unaryExpNode.primaryExpNode;
        if (primaryExpNode.numberNode != null || primaryExpNode.characterNode != null) {// 1 / a
            isArray = false;
            return;
        }
        if (primaryExpNode.expNode != null) {// 取决于(exp)中的性质
            isArray = primaryExpNode.expNode.isArray;
            if (isArray == true) {
                varType = primaryExpNode.expNode.varType;
            }
            return;
        }
        LValNode lValNode = primaryExpNode.lValNode;
        if (lValNode.arrayNode != null) {// a[0]
            isArray = false;
            return;
        }
        if (lValNode.varSymbol != null) {
            isArray = lValNode.varSymbol.isArray();
            varType = lValNode.varSymbol.getVarType();
        }
    }

    @Override
    public String toString() {
        return "<Exp>";
    }

    private ExpNode() {}
}
