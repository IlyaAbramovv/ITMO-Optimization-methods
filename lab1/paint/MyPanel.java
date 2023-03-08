package lab1.paint;

import lab1.GradientDescent;
import lab1.functions.Function;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MyPanel extends JPanel implements MouseWheelListener {
    private final int width, height;
    private int x0, y0;
    private final Function function;
    private double scale;
    private final int HORIZONTAL_SIZE = 10;
    private final int VERTICAL_SIZE = 10;

    public MyPanel(int width, int height, Function function, Function level) {
        setLayout(null);
        setPreferredSize(new Dimension(width, height));
        x0 = width / 2;
        y0 = height / 2;
        this.width = width;
        this.height = height;
        this.function = function;
        scale = 100;
        addMouseWheelListener(this);
        Timer timer = new Timer(5, null);
        timer.addActionListener(e -> repaint());
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        drawLine(g2d, -HORIZONTAL_SIZE, 0, HORIZONTAL_SIZE, 0);
        drawLine(g2d, 0, -VERTICAL_SIZE, 0, VERTICAL_SIZE);
        for (int i = -VERTICAL_SIZE; i <= VERTICAL_SIZE; i++) {
            if (i == 0) {
                continue;
            }
            g2d.drawLine(x0 - 2, (int) (height - y0 + i * scale), x0 + 2, (int) (height - y0 + i * scale));
            g2d.drawString(String.valueOf(-i), x0 + 5, (int) (height - y0 + i * scale) + 4);
        }
        for (int i = -HORIZONTAL_SIZE; i <= HORIZONTAL_SIZE; i++) {
            if (i == 0) {
                continue;
            }
            g2d.drawLine((int) (x0 + i * scale), height - y0 + 2, (int) (x0 + i * scale), height - y0 - 2);
            g2d.drawString(String.valueOf(i), (int) (x0 + i * scale) - 3, height - y0 + 14);
        }
        g2d.setColor(Color.BLUE);
        var res = GradientDescent.gradientDescent(function, true);
        for (var beb : res) {
            double x0 = -3, x1 = 8;
            double h = 0.00002;
            double level = function.evaluate(beb);
            while (x0 + h < x1) {
                double y0 = (-2 - x0 + Math.sqrt(-3 * x0 * x0 + 16 * x0 - 12 + 4 * level)) / 2.0;
                x0 += h;
                double y1 = (-2 - x0 - Math.sqrt(-3 * x0 * x0 + 16 * x0 - 12 + 4 * level)) / 2.0;
                drawLine(g2d, y0, x0, y0, x0);
                drawLine(g2d, y1, x0, y1, x0);
            }
        }
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        var first = res.get(0);
        for (int i = 1; i < res.size(); i++) {
            var second = res.get(i);
            drawLine(g2d, second.get("x"), second.get("y"), first.get("x"), first.get("y"));
            first = second;
        }
    }

    public void drawLine(Graphics2D g2d, double x0, double y0, double x1, double y1) {
        g2d.drawLine((int) (x0 * scale + this.x0), (int) (height - (y0 * scale + this.y0)), (int) (x1 * scale + this.x0), (int) (height - (y1 * scale + this.y0)));
    }

    public void moveLeft() {
        x0 -= 5;
    }

    public void moveRight() {
        x0 += 5;
    }

    public void moveUp() {
        y0 += 5;
    }

    public void moveDown() {
        y0 -= 5;
    }

    public void zoomIn() {
        scale *= 1.2;
    }

    public void zoomOut() {
        scale /= 1.2;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            zoomIn();
        } else {
            zoomOut();
        }
    }
}
