package bad.robot.radiate.ui;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class CompositeLayerUI<C extends JComponent> extends LayerUI<C> {
    
    private final List<LayerUI<C>> layers;

    public static <C extends JComponent> LayerUI<C> layers(LayerUI<C>... layers) {
        return new CompositeLayerUI<>(layers);
    }

    public CompositeLayerUI(LayerUI<C>... layers) {
        this.layers = Arrays.asList(layers);
    }

    @Override
    public void installUI(JComponent component) {
        for (LayerUI<C> layer : layers)
            layer.installUI(component);
        super.installUI(component);
    }

    @Override
    public void paint(Graphics graphics, JComponent component) {
        super.paint(graphics, component);
        for (LayerUI<C> layer : layers)
            layer.paint(graphics, component);
    }

    @Override
    public void uninstallUI(JComponent component) {
        for (LayerUI<C> layer : layers)
            layer.uninstallUI(component);
        super.uninstallUI(component);
    }
}
