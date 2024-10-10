import java.util.ArrayList;

public class SelfTest {
    public static void main(String[] args) {
        ArrayList<String> strs = new ArrayList<>();
        for (String string : strs) {
            System.out.println(string);
        }
    }

}

class A {
    public String toString() {
        return this.getClass().toString();
    }
}

class B extends A {

}