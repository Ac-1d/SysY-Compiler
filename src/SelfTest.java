public class SelfTest {
    public static void main(String[] args) {
        A.B b = A.B.foo();
    }

    public static Object foo(Object o) {
        return o;
    }
}

class A {
    public void sayHello() {
        System.out.println("hello");
    }

    class B {
        public static B foo() {
            return (new A()).new B();
        }
    }
}