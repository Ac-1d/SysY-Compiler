import java.util.ArrayList;

public class SelfTest {
    public static void main(String[] args) throws ClassNotFoundException {
        ArrayList<A> AsList = new ArrayList<>();
        AsList.add(new A());
        AsList.add(new B());
        for (A a : AsList) {
            System.out.println(a.getClass().equals(Class.forName("A")));
        }
        
    }

}

class A {
    
}

class B extends A {

}
