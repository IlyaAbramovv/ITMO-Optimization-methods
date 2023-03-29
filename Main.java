

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var res = Minimization.linearRegression(List.of(1.0,2.0,3.0,4.0,5.0), List.of(1.0,3.0,2.0,4.0,2.0));
        System.out.println("a: " + res[0]);
        System.out.println("b: " + res[1]);
    }
}
