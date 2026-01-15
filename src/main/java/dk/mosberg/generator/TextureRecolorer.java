package dk.mosberg.generator;

import java.awt.Color;
import java.awt.image.BufferedImage;

public final class TextureRecolorer {

    private TextureRecolorer() {}

    /**
     * Multiply-tint recolor (keeps alpha).
     */
    public static BufferedImage recolorMultiply(BufferedImage base, Color tint) {
        int width = base.getWidth();
        int height = base.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int tr = tint.getRed();
        int tg = tint.getGreen();
        int tb = tint.getBlue();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = base.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb) & 0xFF;

                int nr = (r * tr) / 255;
                int ng = (g * tg) / 255;
                int nb = (b * tb) / 255;

                int out = (a << 24) | (nr << 16) | (ng << 8) | nb;
                result.setRGB(x, y, out);
            }
        }

        return result;
    }
}
