package bad.robot.radiate.ui;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import static java.awt.AlphaComposite.SRC_OVER;
import static java.awt.AlphaComposite.getInstance;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.Color.white;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

class BusyIndicator extends LayerUI<JPanel> implements ActionListener {

    private boolean running;
    private boolean fadingOut;
    private Timer timer;

    private int angle;
    private int fadeCount;
    private int fadeLimit = 15;

    @Override
    public void paint(Graphics g, JComponent component) {
        int width = component.getWidth();
        int height = component.getHeight();
        super.paint(g, component);
        if (!running)
            return;
        Graphics2D graphics = (Graphics2D) g.create();
        float fade = (float) fadeCount / (float) fadeLimit;
        grayOutPanel(width, height, graphics, fade);
        drawWaitIndicator(width, height, graphics, fade);
        graphics.dispose();
    }

    private void drawWaitIndicator(int width, int height, Graphics2D graphics, float fade) {
        int s = Math.min(width, height) / 5;
        int cx = width / 2;
        int cy = height / 2;
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(s / 4, CAP_ROUND, JOIN_ROUND));
        graphics.setPaint(white);
        graphics.rotate(Math.PI * angle / 180, cx, cy);
        for (int i = 0; i < 12; i++) {
            float scale = (11.0f - (float) i) / 11.0f;
            graphics.drawLine(cx + s, cy, cx + s * 2, cy);
            graphics.rotate(-Math.PI / 6, cx, cy);
            graphics.setComposite(getInstance(SRC_OVER, scale * fade));
        }
    }

    private void grayOutPanel(int width, int height, Graphics2D graphics, float fade) {
        Composite urComposite = graphics.getComposite();
        graphics.setComposite(getInstance(SRC_OVER, .5f * fade));
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(urComposite);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            firePropertyChange("tick", 0, 1);
            angle += 3;
            if (angle >= 360) {
                angle = 0;
            }
            if (fadingOut) {
                if (--fadeCount == 0) {
                    running = false;
                    timer.stop();
                }
            } else if (fadeCount < fadeLimit) {
                fadeCount++;
            }
        }
    }

    public void start() {
        if (running)
            return;

        running = true;
        fadingOut = false;
        fadeCount = 0;
        int framesPerSecond = 24;
        int frequency = 1000 / framesPerSecond;
        timer = new Timer(frequency, this);
        timer.start();
    }

    public void stop() {
        fadingOut = true;
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent event, JLayer layer) {
        if ("tick".equals(event.getPropertyName()))
            layer.repaint();
    }

}
