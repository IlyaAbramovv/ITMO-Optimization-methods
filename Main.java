import functions.*;
import lab2.GradientDescentMode;
import lab2.Minimization2;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var res = Minimization2.linearRegression(List.of(1.0, 2.0, 3.0, 4.0, 10.0),
                List.of(0.0, 2.0, 4.0, 6.0, 18.0),
                GradientDescentMode.NESTEROV,
                1);
        System.out.println(res[0] + " " + res[1]);
        List<Function> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(new Pow(new Subtract(new Multiply(new Const(1.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
        }
        for (int i = 50; i < 100; i++) {
            list.add(new Pow(new Subtract(new Multiply(new Const(2.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
        }
        for (int i = 100; i < 150; i++) {
            list.add(new Pow(new Subtract(new Multiply(new Const(3.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
        }
        for (int i = 150; i < 200; i++) {
            list.add(new Pow(new Subtract(new Multiply(new Const(4.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
        }
        Sum f = new Sum(list);
        List<Integer> values = List.of(1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100,
                110, 120, 130, 140, 150, 160, 170, 180, 190, 200);
        for (int i : values) {
            Minimization2.gradientDescent(f, i);
        }
    }
}