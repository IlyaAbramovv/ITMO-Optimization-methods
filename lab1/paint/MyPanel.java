package lab1.paint;

import lab1.FunctionUtils;
import lab1.functions.Function;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Map;

public class MyPanel extends JPanel implements MouseWheelListener {
    private final int width, height;
    private int x0, y0;
    private final Function function;
    private double scale;
    private final int HORIZONTAL_SIZE = 10;
    private final int VERTICAL_SIZE = 10;

    public MyPanel(int width, int height, Function function) {
        setLayout(null);
        setPreferredSize(new Dimension(width, height));
        x0 = width / 2;
        y0 = height / 2;
        this.width = width;
        this.height = height;
        this.function = function;
        scale = 50;
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
        String variableName = FunctionUtils.getAllVariables(function).get(0);
        double h = 0.001;
        double x = -HORIZONTAL_SIZE;
        while (x <= HORIZONTAL_SIZE) {
            double y = function.evaluate(Map.of(variableName, x));
            drawLine(g2d, x, y, x, y);
            x += h;
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
