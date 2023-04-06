import functions.*;
import lab2.GradientDescentMode;
import lab2.Minimization2;

import java.util.ArrayList;
import java.util.List;

import static lab2.GradientDescentMode.*;
import static lab2.Minimization2.*;

public class Main {
    public static void main(String[] args) {
        List<Function> list = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            list.add(new Pow(new Subtract(new Multiply(new Const(1.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
//        }
//        for (int i = 50; i < 100; i++) {
//            list.add(new Pow(new Subtract(new Multiply(new Const(2.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
//        }
//        for (int i = 100; i < 150; i++) {
//            list.add(new Pow(new Subtract(new Multiply(new Const(3.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
//        }
//        for (int i = 150; i < 200; i++) {
//            list.add(new Pow(new Subtract(new Multiply(new Const(4.0), new Variable("x" + i)), new Const(1.0)), new Const(2.0)));
//        }

        for (int i = 1; i < 4; i++) {
            list.add(new Sum(List.of(
                    new Multiply(new Pow(
                            new Subtract(
                                    new Pow(
                                            new Variable("x" + (2 * i - 1)),
                                            new Const(2.0)),
                                    new Variable("x" + 2 * i)),
                            new Const(2.0)), new Const(100.0)),
                    new Pow(
                            new Subtract(
                                    new Variable("x" + (2 * i - 1)),
                                    new Const(1.0)),
                            new Const(2.0)
                    ))));
        }
        Sum f = new Sum(list);

        var list2 = new ArrayList<Function>();
        for (int i = 1; i < 6; i++) {
            list2.add(
                    new Sum(
                            List.of(new Multiply(
                                            new Pow(
                                                    new Subtract(
                                                            new Variable("x" + (i + 1)),
                                                            new Pow(new Variable("x" + i), new Const(2.0))
                                                    ),
                                                    new Const(2.0)
                                            ),
                                            new Const(100.0)
                                    ),
                                    new Pow(
                                            new Subtract(
                                                    new Const(1.0),
                                                    new Variable("x" + i)
                                            ),
                                            new Const(2.0)
                                    ))
                    )
            );
        }

        Sum f2 = new Sum(list2);
        List<Integer> values = List.of(1, 2, 3, 4);
        for (var mode : GradientDescentMode.values()) {
            if (mode == COMMON) continue;
            System.out.println(mode.name());
            for (int i : values) {
                switch (mode) {
                    case NESTEROV -> nesterovGradientDescent(f2, i);
                    case MOMENTUM -> momentumGradientDescent(f2, i);
                    case RMSPROP -> rmsPropGradientDescent(f2, i);
                    case ADAPTIVE -> adaptiveGradientDescent(f2, i);
                    case ADAM -> adamGradientDescent(f2, i);
                }
            }
            System.out.println();
        }
    }
}