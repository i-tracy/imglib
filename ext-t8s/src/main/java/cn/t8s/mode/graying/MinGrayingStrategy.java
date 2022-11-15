package cn.t8s.mode.graying;

import cn.core.impl.mode.AbstractGrayingStrategy;

/**
 * 最小值灰度化策略
 *
 * @author tracy
 * @since 1.0.0
 */
public class MinGrayingStrategy extends AbstractGrayingStrategy {
    @Override
    public int getGraynessValue(int r, int g, int b) {
        return Math.min(Math.min(r, g), b);
    }
}
