import java.util.Random;

public class R {
    public static void main(String[] args) {
        double[] x = new double[1000];
        double[] y = new double[1000];
        Random rand = new Random();

        // Generate x values
        for (int i = 0; i < x.length; i++) {
            x[i] = rand.nextDouble() * 30 - 14; // Generate random x values between -5 and 5
        }

        // Calculate y values
        for (int i = 0; i < x.length; i++) {
            y[i] = 3 * Math.sin(0.6 *x[i]) + 4 - Math.abs(0.6 * x[i]) + 2;
            y[i] += rand.nextGaussian() * 0.5; // Add some random noise to y values
        }

        // Print x and y values
        for (int i = 0; i < x.length; i++) {
            System.out.println("new Main.Point(" + x[i] + ", " + y[i] + "),");
        }
    }
}