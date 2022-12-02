package cn.usage;

import cn.core.BufferedImageEncoder;
import cn.core.PipeFilter;
import cn.core.ex.HandlingException;
import cn.core.ex.InvalidSettingException;
import cn.core.utils.BufferedImageUtils;
import cn.core.utils.CollectionUtils;
import cn.core.utils.ObjectUtils;
import cn.core.utils.StringUtils;
import net.coobird.thumbnailator.Thumbnails;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 管道构造器
 *
 * @author tracy
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSourceBuilder<T> {

    public static final String NULL_FILTER = "PipeFilter is null";
    private final T typeThis = (T) this;
    protected List<PipeFilter> filters = new ArrayList<>();

    /**
     * 输出格式
     */
    protected String formatName;

    public T formatName(String formatName) {
        if (StringUtils.isEmpty(formatName)) {
            throw new InvalidSettingException("format name can not be null");
        }
        this.formatName = formatName;
        return typeThis;
    }

    public T addFilter(PipeFilter ypf) {
        ObjectUtils.excNull(ypf, NULL_FILTER);
        filters.add(ypf);
        return typeThis;
    }

    public T addFilter(PipeFilter... ypf) {
        ObjectUtils.excNull(ypf, NULL_FILTER);
        filters.addAll(Arrays.asList(ypf));
        return typeThis;
    }

    public T removeFilter(PipeFilter ypf) {
        ObjectUtils.excNull(ypf, NULL_FILTER);
        filters.remove(ypf);
        return typeThis;
    }

    /**
     * 获得一张图像
     * @return 图像
     * @throws IOException IO异常
     */
    public BufferedImage obtainBufferedImage() throws IOException {
        List<BufferedImage> images = obtainBufferedImages();
        if (CollectionUtils.isNullOrEmpty(images)) {
            throw new HandlingException("no images were found");
        }
        if (images.size() > 1) {
            throw new HandlingException("cannot create one image from multiple original gif frames");
        }
        return images.get(0);
    }

    /**
     * 获取图像
     * @return 图像集合
     * @throws IOException IO异常
     */
    public List<BufferedImage> obtainBufferedImages() throws IOException {
        List<BufferedImage> sourceImages = obtainSourceImages();
        if (CollectionUtils.isNullOrEmpty(sourceImages)) {
            throw new HandlingException("no images were found");
        }
        // execute all filters
        List<BufferedImage> targetImages = sourceImages;
        for (PipeFilter ypf : filters) {
            targetImages = ypf.execute(sourceImages);
            sourceImages = targetImages;
        }
        return targetImages;
    }

    /**
     * 获取图像源
     * @return 图像集合
     * @throws IOException IO异常
     */
    protected abstract List<BufferedImage> obtainSourceImages() throws IOException;


    public Thumbnails.Builder<BufferedImage> toThumbnails() throws IOException {
        BufferedImage[] images = obtainBufferedImages().toArray(new BufferedImage[0]);
        return Thumbnails.of(images);
    }

    public void toFile(BufferedImageEncoder encoder) throws IOException {
        if (encoder.supportMultiple()) {
            encoder.encode(obtainBufferedImages());
        } else {
            encoder.encode(Collections.singletonList(obtainBufferedImage()));
        }
    }

    public void toFile(File dest) throws IOException {
        BufferedImage image = obtainBufferedImage();
        File f = choseFormat(dest);
        BufferedImageUtils.write(image, formatName, f);
    }

    public void toFile(String fn) throws IOException {
        toFile(new File(fn));
    }

    public void toFiles(Iterator<File> fIter) throws IOException {
        if (fIter == null) {
            throw new NullPointerException("File iterator is null");
        }

        List<BufferedImage> images = obtainBufferedImages();
        for (BufferedImage o : images) {
            if (!fIter.hasNext()) {
                throw new IndexOutOfBoundsException("not enough File provided by iterator");
            }
            File f = choseFormat(fIter.next());
            BufferedImageUtils.write(o, formatName, f);
        }
    }

    public void toFiles(String... filenames) throws IOException {
        if (filenames == null) {
            throw new NullPointerException("File names is null");
        }

        List<BufferedImage> images = obtainBufferedImages();
        if (images.size() > filenames.length) {
            throw new IndexOutOfBoundsException("not enough File name provided by iterator");
        }
        for (int i = 0; i < filenames.length; i++) {
            File f = choseFormat(new File(filenames[i]));
            BufferedImageUtils.write(images.get(i), formatName, f);
        }
    }

    public void toOutputStream(OutputStream os) throws IOException {
        BufferedImage image = obtainBufferedImage();
        ImageIO.write(image, formatName, os);
    }

    public void toOutputStreams(Iterator<OutputStream> osIter) throws IOException {
        if (osIter == null) {
            throw new NullPointerException("OutputStream iterator is null");
        }

        List<BufferedImage> images = obtainBufferedImages();
        for (BufferedImage o : images) {
            if (!osIter.hasNext()) {
                throw new IndexOutOfBoundsException("not enough OutputStream provided by iterator");
            }
            ImageIO.write(o, formatName, osIter.next());
        }
    }

    private File choseFormat(File f) {
        if (f == null) {
            throw new NullPointerException("output file is null");
        }
        String extension = StringUtils.getExtensionName(f);

        if (StringUtils.isEmpty(formatName)) {
            if (StringUtils.isEmpty(extension)) {
                throw new InvalidSettingException("no output format was specified");
            }
            // initial the output format name
            formatName = extension;
        } else {
            // adjust the output file's if necessary
            if (!extension.equalsIgnoreCase(formatName)) {
                String newFilename = f.getAbsolutePath() + "." + formatName;
                return new File(newFilename);
            }
        }
        return f;
    }

}