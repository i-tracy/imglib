package cn.extension.ext.shape;

import cn.extension.exec.InvalidSettingException;
import cn.extension.ext.OverlayShape;
import cn.extension.tool.GenericBuilder;

import java.awt.*;

/**
 * 封闭图像
 *
 * @author tracy
 * @since 1.0.0
 */
public abstract class AbstractClosedShape implements OverlayShape {

    /**
     * 绘制区域
     */
    protected Rectangle rect;

    /**
     * 笔画样式，例如 BasicStroke
     */
    protected Stroke stroke;

    /**
     * 是否填充
     */
    protected boolean fill;

    /**
     * 颜色
     */
    protected Color color;


    public AbstractClosedShape(ClosedShapeBuilder csb) {
        this.rect = csb.rect;
        this.stroke = csb.stroke;
        this.fill = csb.fill;
        this.color = csb.color;
    }

    @Override
    public void paint(int canvasWidth, int canvasHeight, Graphics2D g2d) {
        // check for rect is out of bounds
        if (rect == null) {
            throw new InvalidSettingException("not specified any rect for this shape");
        } else {
            int maxX = rect.x + rect.width;
            int maxY = rect.y + rect.height;
            if (maxX > canvasWidth || maxY > canvasHeight) {
                throw new InvalidSettingException("the rect is out of image");
            }
        }

        if (fill) {
            // the default fill color is white
            g2d.setColor(color == null ? Color.WHITE : color);
            fillInside(canvasWidth, canvasHeight, g2d);
        } else {
            // the default draw color is black
            g2d.setColor(color == null ? Color.BLACK : color);
            // the default store is basic stroke
            g2d.setStroke(stroke == null ? new BasicStroke() : stroke);
            drawBorder(canvasWidth, canvasHeight, g2d);
        }
    }

    /**
     * 绘制边框
     * @param canvasWidth 画笔宽
     * @param canvasHeight 画笔高
     * @param g2d 画笔
     */
    public abstract void drawBorder(int canvasWidth, int canvasHeight, Graphics2D g2d);

    /**
     * 填充内部
     * @param canvasWidth 画笔宽
     * @param canvasHeight 画笔高
     * @param g2d 画笔
     */
    public abstract void fillInside(int canvasWidth, int canvasHeight, Graphics2D g2d);


    public abstract static class ClosedShapeBuilder implements GenericBuilder<AbstractClosedShape> {
        protected Rectangle rect;
        protected Stroke stroke;
        protected boolean fill;
        protected Color color;

        public ClosedShapeBuilder rect(Rectangle rect) {
            this.rect = rect;
            return this;
        }
        public ClosedShapeBuilder stroke(Stroke stroke){
            this.stroke = stroke;
            return this;
        }
        public ClosedShapeBuilder fill(boolean fill){
            this.fill = fill;
            return this;
        }
        public ClosedShapeBuilder color(Color color){
            this.color = color;
            return this;
        }
    }
}