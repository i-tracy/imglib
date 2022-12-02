package cn.usage.builder;

import cn.usage.AbstractSourceBuilder;
import cn.core.ex.HandlingException;
import cn.core.ex.InvalidSettingException;
import cn.core.in.GifSource;
import cn.core.tool.Range;
import cn.core.utils.CollectionUtils;
import cn.core.utils.StringUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GifSourceBuilder
 *
 * @author tracy
 * @since 1.0.0
 */
public class GifSourceBuilder<S> extends AbstractSourceBuilder<GifSourceBuilder<S>> {

    protected final GifSource<S> source;
    private boolean containsAll = false;
    private final Set<Integer> frames = new HashSet<>();

    public GifSourceBuilder(GifSource<S> gifSource) {
        this.source = gifSource;
    }

    public GifSourceBuilder<S> registerAll() {
        containsAll = true;
        return this;
    }

    public GifSourceBuilder<S> register(int frameIndex) {
        frames.add(frameIndex);
        return this;
    }

    public GifSourceBuilder<S> register(int... frameIndexes) {
        for (int index : frameIndexes) {
            frames.add(index);
        }
        return this;
    }

    public GifSourceBuilder<S> register(Range<Integer> range) {
        for (int i = range.getMin(); i <= range.getMax(); i++) {
            frames.add(i);
        }
        return this;
    }

    @Override
    protected List<BufferedImage> obtainSourceImages() throws IOException {
        checkReadiness();

        // the max frame size index of the gif
        int maxFrameIndex = source.size() - 1;

        // export all frame
        if (containsAll) {
            return source.readAll();
        }

        // export specified frames
        // check all frame was in bound
        Set<String> invalidPages = frames.stream()
                .filter(p -> maxFrameIndex < p)
                .map(Objects::toString)
                .collect(Collectors.toSet());
        if (!CollectionUtils.isNullOrEmpty(invalidPages)) {
            throw new InvalidSettingException(MessageFormat.format(
                    "the frame indexes:[{0}] has exceeded the max size of the gif document",
                    StringUtils.join(invalidPages)));
        }
        return source.read(frames.toArray(new Integer[0]));
    }

    protected void checkReadiness() {
        if (CollectionUtils.isNullOrEmpty(frames) && !containsAll) {
            throw new HandlingException("no frame to export");
        }
    }
}
