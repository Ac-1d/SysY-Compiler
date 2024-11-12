import java.util.ArrayList;

public class SelfTest {
    public static void main(String[] args) throws ClassNotFoundException {
        foo(Integer.valueOf(10));
        foo(20);
        foo(null);
    }

    static void foo(Integer a) {
        Integer b = a;
    }
}