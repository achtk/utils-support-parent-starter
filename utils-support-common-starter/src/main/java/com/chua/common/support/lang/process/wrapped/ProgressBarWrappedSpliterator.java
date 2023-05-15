package com.chua.common.support.lang.process.wrapped;

import com.chua.common.support.lang.process.ProgressBar;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.function.Consumer;

/**
 * Any spliterator whose parallel iteration is tracked by a multi-threaded progress bar.
 *
 * @author Tongfei Chen
 * @since 0.7.2
 */
@EqualsAndHashCode
public class ProgressBarWrappedSpliterator<T> implements Spliterator<T>, AutoCloseable {

    private Spliterator<T> underlying;
    private ProgressBar pb;
    private Set<Spliterator<T>> openChildren;

    public ProgressBarWrappedSpliterator(Spliterator<T> underlying, ProgressBar pb) {
        this(underlying, pb, Collections.synchronizedSet(new HashSet<>()));
    }

    private ProgressBarWrappedSpliterator(Spliterator<T> underlying, ProgressBar pb, Set<Spliterator<T>> openChildren) {
        this.underlying = underlying;
        this.pb = pb;
        this.openChildren = openChildren;
        this.openChildren.add(this);
    }

    public ProgressBar getProgressBar() {
        return pb;
    }

    @Override
    public void close() {
        pb.close();
    }

    private void registerChild(Spliterator<T> child) {
        openChildren.add(child);
    }

    private void removeThis() {
        openChildren.remove(this);
        if (openChildren.size() == 0) {
            close();
        }
        // only closes the progressbar if no spliterator is working anymore
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        boolean r = underlying.tryAdvance(action);
        if (r) {
            pb.step();
        } else {
            removeThis();
        }
        return r;
    }

    @Override
    public Spliterator<T> trySplit() {
        Spliterator<T> u = underlying.trySplit();
        if (u != null) {
            ProgressBarWrappedSpliterator<T> child = new ProgressBarWrappedSpliterator<>(u, pb, openChildren);
            registerChild(child);
            return child;
        } else {
            return null;
        }
    }

    @Override
    public long estimateSize() {
        return underlying.estimateSize();
    }

    @Override
    public int characteristics() {
        return underlying.characteristics();
    }

    @Override
    public Comparator<? super T> getComparator() {
        return underlying.getComparator();
    }

}
