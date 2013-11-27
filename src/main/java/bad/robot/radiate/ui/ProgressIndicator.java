package bad.robot.radiate.ui;

import bad.robot.radiate.Activity;
import bad.robot.radiate.Progress;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Arc2D;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static bad.robot.radiate.Activity.Progressing;
import static bad.robot.radiate.ui.FrameRate.videoFramesPerSecond;
import static bad.robot.radiate.ui.Swing.*;
import static java.awt.AlphaComposite.SRC_OVER;
import static java.awt.AlphaComposite.getInstance;
import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.Color.white;
import static java.awt.RenderingHints.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

class ProgressIndicator extends LayerUI<JComponent> implements ActionListener {

    public static final int maximum = 100;
    private Progress progress = new Progress(0, maximum);
    private Timer timer = new Timer(videoFramesPerSecond.asFrequencyInMillis(), this);
    private int animationLimit = 0;

    @Override
    public void paint(Graphics g, JComponent component) {
        super.paint(g, component);
        if (!timer.isRunning())
            return;
        Graphics2D graphics = (Graphics2D) g.create();
        Rectangle drawArea = getDrawAreaAndCenterWithin(component);
        drawProgressIndicator(drawArea, graphics);
        graphics.dispose();
    }

    private Rectangle getDrawAreaAndCenterWithin(JComponent component) {
        Rectangle drawArea = getReducedRegionAsSquare(component, 20);
        centerRegionWithinComponent(component, drawArea);
        return drawArea;
    }

    private void drawProgressIndicator(Rectangle region, Graphics2D graphics) {
        setLineWidth(region, graphics);
        drawBackgroundRadial(region, graphics);
        drawProgressRadial(region, graphics);
        drawPercentage(region, progress, graphics);
    }

    private void setLineWidth(Rectangle region, Graphics2D graphics) {
        float size = Math.min(region.width, region.height) * 0.10f;
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_PURE); // stops the wobble
        graphics.setStroke(new BasicStroke(Math.max(1, size), CAP_BUTT, JOIN_ROUND));
    }

    private void drawBackgroundRadial(final Rectangle region, final Graphics2D graphics) {
        applyWithComposite(graphics, getInstance(SRC_OVER, 0.20f), new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                graphics.setColor(white);
                graphics.drawArc(region.x, region.y, region.width, region.height, 90, 360);
                return null;
            }
        });
    }

    private void drawProgressRadial(Rectangle region, Graphics2D graphics) {
        graphics.setPaint(white);
        graphics.draw(new Arc2D.Double(region.x, region.y, region.width, region.height, 90, progress.asAngle(), Arc2D.OPEN));
    }

    private void drawPercentage(Rectangle parent, Progress progress, Graphics2D graphics) {
        Font font = new Font("Arial", Font.PLAIN, 12);
        Rectangle region = getReducedRegion(parent, 80);
        setFontScaledToRegion(region, graphics, progress.toString(), font);

        FontRenderContext renderContext = graphics.getFontRenderContext();
        GlyphVector vector = graphics.getFont().createGlyphVector(renderContext, progress.toString());
        Rectangle visualBounds = vector.getVisualBounds().getBounds();

        Double x = region.x + (parent.width / 2) - (visualBounds.getWidth() / 2);
        Double y = region.y + (parent.height / 2) + (visualBounds.getHeight() / 2);
        graphics.drawString(progress.toString(), x.floatValue(), y.floatValue());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (timer.isRunning()) {
            if (progress.lessThan(animationLimit))
                progress.increment();
            else if (progress.greaterThan(animationLimit))
                progress.decrement();
            repaint();
            if (progress.complete())
                timer.stop();
        }
    }

    private void repaint() {
        firePropertyChange("tick", 0, 1);
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent event, JLayer layer) {
        if ("tick".equals(event.getPropertyName()))
            layer.repaint();
    }

    public void setVisiblityBasedOn(Activity activity, Progress progress) {
        if (activity == Progressing) {
            setProgress(progress);
            timer.start();
        } else {
            timer.stop();
        }
    }

    private Progress progressCache = progress;
    private void setProgress(Progress progress) {
        if (!progressCache.toString().equals(progress.toString())) {
            System.out.println("setting progress = " + progress);
            progressCache = progress;
        }
        animationLimit = Math.min(progress.current(), maximum);
    }

    public static class Example {

        public static void main(String[] args) {
            ProgressIndicator indicator = setupWindow();
            updateProgressInAThread(indicator);
        }

        private static ProgressIndicator setupWindow() {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            JPanel panel = new JPanel() {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    Graphics2D graphics = (Graphics2D) g.create();
                    Swing.drawCentreLines(this.getBounds(), graphics);
                    graphics.dispose();
                }
            };
            panel.setBackground(Color.lightGray);
            ProgressIndicator indicator = new ProgressIndicator();
            frame.add(new JLayer<>(panel, indicator));
            frame.setVisible(true);
            return indicator;
        }

        private static void updateProgressInAThread(final ProgressIndicator indicator) {
            ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
            final Integer[] progress = {0};
            threadPool.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    boolean goneBackwards = false;
                    progress[0] = progress[0] + 10;
                    if (progress[0] > 50 && !goneBackwards)
                        goneBackwards = true;
                    if (goneBackwards)
                        progress[0] = progress[0] = 16;
                    indicator.setVisiblityBasedOn(Progressing, new Progress(progress[0], maximum));
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }
}
