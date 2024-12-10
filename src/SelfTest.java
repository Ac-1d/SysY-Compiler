import java.util.ArrayDeque;
import java.util.Deque;

public class SelfTest {
    public static void main(String[] args) throws ClassNotFoundException {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        System.out.println(stack);
        System.out.println(stack.peek());
        System.out.println(stack.pop());
    }

}