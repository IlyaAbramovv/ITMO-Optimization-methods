package lab1.paint;

import lab1.functions.Function;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyFrame extends JFrame {
    public MyFrame(Function function) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        MyPanel panel = new MyPanel(width, height, function);
        add(panel);
        pack();
        setVisible(true);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    panel.moveLeft();
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    panel.moveDown();
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    panel.moveUp();
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    panel.moveRight();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
}
