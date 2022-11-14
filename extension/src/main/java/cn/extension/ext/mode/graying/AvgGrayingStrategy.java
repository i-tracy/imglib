package cn.extension.ext.mode.graying;

import cn.extension.ext.mode.AbstractGrayingStrategy;

/**
 * 平均值灰度化策略
 *
 * @author tracy
 * @since 1.0.0
 */
public class AvgGrayingStrategy extends AbstractGrayingStrategy {
    @Override
    public int getGraynessValue(int r, int g, int b) {
        return (r + g + b) / 3;
    }
}