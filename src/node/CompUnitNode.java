package node;

import java.util.ArrayList;

import frontend.Parse;

public class CompUnitNode {
    // CompUnit → {Decl} {FuncDef} MainFuncDef
    private ArrayList<DeclNode> declNodes = new ArrayList<>();
    private ArrayList<FuncDefNode> funcDefNodes = new ArrayList<>();
    private MainFuncDefNode mainFuncDefNode;

    public static CompUnitNode CompUnit() {//finish
        Parse instance = Parse.getInstance();
        CompUnitNode compUnit = new CompUnitNode();
        DeclNode declNode;
        FuncDefNode funcDefNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        while((declNode = DeclNode.Decl()) != null) {
            compUnit.declNodes.add(declNode);
            tmpIndex = instance.getPeekIndex();
        }
        instance.setPeekIndex(tmpIndex);
        while((funcDefNode = FuncDefNode.FuncDef()) != null) {
            compUnit.funcDefNodes.add(funcDefNode);
            tmpIndex = instance.getPeekIndex();
        }
        instance.setPeekIndex(tmpIndex);
        compUnit.mainFuncDefNode = MainFuncDefNode.MainFuncDef();
        return compUnit;
    }

    private CompUnitNode() {}

}
