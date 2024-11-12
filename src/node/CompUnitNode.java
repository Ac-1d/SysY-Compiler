package node;

import java.util.ArrayList;

import frontend.LLVMGenerator;
import frontend.Parser;

public class CompUnitNode {
    // CompUnit → {Decl} {FuncDef} MainFuncDef

    ArrayList<DeclNode> declNodes = new ArrayList<>();
    ArrayList<FuncDefNode> funcDefNodes = new ArrayList<>();
    MainFuncDefNode mainFuncDefNode;

    public static CompUnitNode CompUnit() {//finish
        Parser instance = Parser.getInstance();
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

    public void print() {
        for (DeclNode declNode : declNodes) {
            declNode.print();
        }
        for (FuncDefNode funcDefNode : funcDefNodes) {
            funcDefNode.print();
        }
        mainFuncDefNode.print();
        System.out.println(this.toString());
    }

    public void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        llvmGenerator.setIsDeclVarGlobal(true);
        for (DeclNode declNode : declNodes) {
            declNode.makeLLVM();
        }
        llvmGenerator.setIsDeclVarGlobal(false);
        for (FuncDefNode funcDefNode : funcDefNodes) {
            funcDefNode.setupSymbolTable();
        }
        mainFuncDefNode.makeLLVM();
    }

    public String toString() {
        return "<CompUnit>";
    }

    private CompUnitNode() {}

}
