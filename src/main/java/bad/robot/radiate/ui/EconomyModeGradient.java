package bad.robot.radiate.ui;

import java.awt.*;

class EconomyModeGradient extends GradientPaint {
    public EconomyModeGradient(float x1, float y1, Color color1, float x2, float y2, Color color2) {
        super(x1, y1, darken(color1, 5), x2, y2, darken(color2, 2));
    }

    private static Color darken(Color color, int amount) {
        Color darkened = color;
        for (int i = 0; i < amount; i++)
            darkened = darkened.darker();
        return darkened;
    }
}
