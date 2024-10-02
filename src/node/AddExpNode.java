package node;

public class AddExpNode {
    // AddExp → MulExp | AddExp ('+' | '−') MulExp 
    /*  change it to AddExp → MulExp | MulExp ('+' | '−')  AddExp 
     *  and rechange in print()
    */
    

    public static AddExpNode AddExp() {
        AddExpNode addExpNode = new AddExpNode();

        return addExpNode;
    }
}
