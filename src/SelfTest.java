
public class SelfTest {
    public static void main(String[] args) {
        A a1 = new A(null, 1);
        A a2 = new A(a1, 2);
        System.out.println(a1.lowerA.toString());
        System.out.println(a2.upperA.toString());
        
    }

}

class A {
    public A upperA;
    public A lowerA;
    public int num = 0;

    public A(A upperA, int num) {
        if(upperA != null) {
            this.upperA = upperA;
            upperA.lowerA = this;
        }
        this.num = num;
    }

    @Override
    public String toString() {
        return num + "";
    }

}
