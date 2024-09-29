package node;

import java.util.ArrayList;

public class CompUnitNode {
    // CompUnit → {Decl} {FuncDef} MainFuncDef
    private ArrayList<DeclNode> declNodes = new ArrayList<>();
    private ArrayList<FuncDefNode> funcDefNodes = new ArrayList<>();
    private MainFuncDefNode mainFuncDefNode;

    public static CompUnitNode CompUnit() {
        CompUnitNode compUnit = new CompUnitNode();
        DeclNode declNode;
        while((declNode = DeclNode.Decl()) != null) {
            compUnit.declNodes.add(declNode);
        }
        FuncDefNode funcDefNode;
        while((funcDefNode = FuncDefNode.FuncDef()) != null) {
            compUnit.funcDefNodes.add(funcDefNode);
        }
        compUnit.mainFuncDefNode = MainFuncDefNode.MainFuncDef();

        return compUnit;
    }

}
