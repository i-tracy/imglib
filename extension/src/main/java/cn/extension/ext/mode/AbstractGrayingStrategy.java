package cn.extension.ext.mode;

import cn.extension.ext.ModeStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 抽象的灰度化策略
 *
 * @author tracy
 * @since 1.0.0
 */
public abstract class AbstractGrayingStrategy implements ModeStrategy {

    @Override
    public void execute(BufferedImage img) {
        if (img == null) {
            throw new NullPointerException("image is null");
        }
        int w = img.getWidth();
        int h = img.getHeight();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                int grayness = getGraynessValue(r, g, b);

                int grayRgb = (grayness << 16) | (grayness << 8) | grayness;
                img.setRGB(x, y, grayRgb);
            }
        }
    }


    /**
     * 计算灰度值
     * @param r red
     * @param g green
     * @param b blue
     * @return grayness value
     */
    public abstract int getGraynessValue(int r, int g, int b);

}
