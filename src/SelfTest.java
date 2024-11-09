import java.util.ArrayList;

public class SelfTest {
    public static void main(String[] args) throws ClassNotFoundException {
        A a = new A("init");
        a.setInnerA();
        a.foo();
        System.out.println(a.str);
        System.out.println(a.a.str);
    }
}

class A {
    public String str = "init";
    public A a;

    A(String name) {
        this.str = name;
    }

    void foo() {
        A bar = a;
        bar.str = "change";
        // System.out.println(a.str);
        // System.out.println(bar);
    }

    void setInnerA() {
        a = new A("inner");
    }
}