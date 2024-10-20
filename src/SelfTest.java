import java.util.ArrayList;

public class SelfTest {
    public static void main(String[] args) throws ClassNotFoundException {
        ArrayList<A> AsList = new ArrayList<>();
        AsList.add(new A());
        AsList.add(new B());
        System.out.println(AsList.size());
    }

}

class A {
    
}

class B extends A {

}
