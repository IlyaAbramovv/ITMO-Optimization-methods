

import lab2.GradientDescentMode;
import lab2.Minimization2;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var res = Minimization2.linearRegression(List.of(1.0, 2.0, 3.0, 4.0, 12.0),
                List.of(50.0, 50.0, 50.0, 50.0, 50.0),
                GradientDescentMode.ADAPTIVE,
                1);
        System.out.println("a: " + res[0]);
        System.out.println("b: " + res[1]);
    }
}
