package dk.mosberg.generator;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Utility for recoloring base textures to generate variants.
 */
public class TextureRecolorer {
    /**
     * Recolors a texture image with the given color.
     *
     * @param base Base image
     * @param color Color to apply
     * @return Recolored image
     */
    public static BufferedImage recolor(BufferedImage base, Color color) {
        int width = base.getWidth();
        int height = base.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = base.getRGB(x, y);
                Color pixel = new Color(argb, true);
                // Simple multiply blend
                int r = (pixel.getRed() * color.getRed()) / 255;
                int g = (pixel.getGreen() * color.getGreen()) / 255;
                int b = (pixel.getBlue() * color.getBlue()) / 255;
                int a = pixel.getAlpha();
                Color newPixel = new Color(r, g, b, a);
                result.setRGB(x, y, newPixel.getRGB());
            }
        }
        return result;
    }
}
