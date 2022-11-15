package cn.t8s.mode.graying;

import cn.core.impl.mode.AbstractGrayingStrategy;

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