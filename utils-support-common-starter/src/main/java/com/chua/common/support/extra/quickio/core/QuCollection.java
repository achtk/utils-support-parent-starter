package com.chua.common.support.extra.quickio.core;

import com.chua.common.support.extra.quickio.api.Collection;
import com.chua.common.support.extra.quickio.api.FindOptions;
import com.chua.common.support.extra.quickio.exception.QuException;
import com.google.common.util.concurrent.AtomicDouble;
import org.iq80.leveldb.DBException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.chua.common.support.constant.NumberConstant.NUM_18;

/**
 * 配置
 * @author CH
 */
final class QuCollection<T extends IoEntity> implements Collection<T> {

    private final EngineIo engine;
    private final Indexer indexer;
    private final Class<T> clazz;


    QuCollection(Class<T> clazz, EngineIo engine, Indexer indexer) {
        this.clazz = clazz;
        this.engine = engine;
        this.indexer = indexer;
    }


    @Override
    public void save(T t) {
        if (t.objectId() == 0 || Plugin.getDigit(t.objectId()) < NUM_18) {
            t.id = Plugin.generateId();
            t.createdAt = Plugin.toTimestamp(t.objectId());
        }
        indexer.setIndex(t);
        try {
            engine.put(Codec.encodeKey(t.objectId()), Codec.encode(t));
        } catch (DBException e) {
            indexer.removeIndex(t);
            throw new QuException(e);
        }
    }


    @Override
    public void save(List<T> list) {
        list.forEach(t -> {
            if (t.objectId() == 0 || Plugin.getDigit(t.objectId()) < NUM_18) {
                t.id = Plugin.generateId();
                t.createdAt = Plugin.toTimestamp(t.objectId());
            }
        });
        indexer.setIndexes(list);
        try {
            engine.writeBatch(batch -> list.forEach(t -> batch.put(Codec.encodeKey(t.objectId()), Codec.encode(t))));
        } catch (Exception e) {
            indexer.removeIndexList(list);
            throw new QuException(e);
        }
    }


    @Override
    public void update(T t, Predicate<T> predicate) {
        List<T> newlocaltlist = new ArrayList<>();
        List<T> oldlocaltlist = new ArrayList<>();
        ReflectObject<T> tObject = new ReflectObject<>(t);
        engine.iteration((key, value) -> {
            T localT = Codec.decode(value, clazz);
            if (localT != null && predicate.test(localT)) {
                oldlocaltlist.add(Codec.clone(localT, clazz));
                ReflectObject<T> object = new ReflectObject<>(localT);
                tObject.traverseFields((name, value1) -> Optional.ofNullable(value1).ifPresent(v -> object.setValue(name, v)));
                newlocaltlist.add(object.get());
            }
        });
        indexer.setIndexes(newlocaltlist);
        try {
            engine.writeBatch(batch -> newlocaltlist.forEach(t1 -> batch.put(Codec.encodeKey(t1.objectId()), Codec.encode(t1))));
        } catch (Exception e) {
            indexer.removeIndexList(newlocaltlist);
            indexer.setIndexes(oldlocaltlist);
            throw new QuException(e);
        }
    }


    @Override
    public void updateWithIndex(T t, Consumer<FindOptions> consumer) {
        T localT = findWithIndex(consumer);
        if (localT != null) {
            ReflectObject<T> object = new ReflectObject<>(localT);
            ReflectObject<T> tObject = new ReflectObject<>(t);
            tObject.traverseFields((name, value) -> Optional.ofNullable(value).ifPresent(v -> object.setValue(name, v)));
            save(object.get());
        }
    }


    @Override
    public void delete(long id) {
        engine.delete(Codec.encodeKey(id));
        indexer.removeIndex(id);
    }


    @Override
    public void delete(long... ids) {
        engine.writeBatch(batch -> {
            for (long id : ids) {
                batch.delete(Codec.encodeKey(id));
            }
        });
        indexer.removeIndexes(ids);
    }


    @Override
    public void delete(List<Long> ids) {
        engine.writeBatch(batch -> ids.forEach(id -> batch.delete(Codec.encodeKey(id))));
        indexer.removeIndexes(ids);
    }


    @Override
    public void delete(Predicate<T> predicate) {
        List<Long> ids = new ArrayList<>();
        engine.writeBatch(batch -> engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            if (t != null) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                batch.delete(key);
                ids.add(t.objectId());
            }
        }));
        indexer.removeIndexes(ids);
    }


    @Override
    public void deleteAll() {
        delete((Predicate<T>) null);
    }


    @Override
    public void deleteWithIndex(Consumer<FindOptions> consumer) {
        T t = findWithIndex(consumer);
        Optional.ofNullable(t).ifPresent(t1 -> delete(t1.objectId()));
    }


    @Override
    public List<T> findAll() {
        return find(null, null);
    }


    @Override
    public List<T> find(Predicate<T> predicate, Consumer<FindOptions> consumer) {
        QuFindOptions options = (consumer != null) ? new QuFindOptions() : null;
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(options));
        List<T> list = new ArrayList<>();
        engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            if (t != null) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                list.add(t);
            }
        });
        return (consumer != null) ? options.get(list) : list;
    }


    @Override
    public List<T> find(Predicate<T> predicate) {
        return find(predicate, null);
    }


    @Override
    public List<T> find(List<Long> ids) {
        List<T> list = new ArrayList<>();
        ids.forEach(id -> {
            byte[] key = Codec.encodeKey(id);
            byte[] value = engine.get(key);
            T t = (value != null) ? Codec.decode(value, clazz) : null;
            Optional.ofNullable(t).ifPresent(list::add);
        });
        return list;
    }


    @Override
    public List<T> find(long... ids) {
        List<T> list = new ArrayList<>();
        for (long id : ids) {
            byte[] key = Codec.encodeKey(id);
            byte[] value = engine.get(key);
            T t = (value != null) ? Codec.decode(value, clazz) : null;
            Optional.ofNullable(t).ifPresent(list::add);
        }
        return list;
    }


    @Override
    public List<T> findWithId(Predicate<Long> predicate, Consumer<FindOptions> consumer) {
        QuFindOptions options = (consumer != null) ? new QuFindOptions() : null;
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(options));
        List<T> list = new ArrayList<>();
        engine.iteration((key, value) -> {
            long id = Codec.decodeKey(key);
            if (predicate.test(id)) {
                T t = Codec.decode(value, clazz);
                Optional.ofNullable(t).ifPresent(list::add);
            }
        });
        return (consumer != null) ? options.get(list) : list;
    }


    @Override
    public List<T> findWithId(Predicate<Long> predicate) {
        return findWithId(predicate, null);
    }


    @Override
    public List<T> findWithTime(Predicate<Long> predicate, Consumer<FindOptions> consumer) {
        return findWithId(id -> predicate.test(Plugin.toTimestamp(id)), consumer);
    }


    @Override
    public List<T> findWithTime(Predicate<Long> predicate) {
        return findWithTime(predicate, null);
    }


    @Override
    public T findFirst(Predicate<T> predicate) {
        AtomicReference<T> minT = new AtomicReference<>();
        engine.iteration((key, value) -> {
            long id = Codec.decodeKey(key);
            T t = Codec.decode(value, clazz);
            boolean b = t != null && (minT.get() == null || id < minT.get().objectId());
            if (b) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                minT.set(t);
            }
        });
        return minT.get();
    }


    @Override
    public T findFirst() {
        return findFirst(null);
    }


    @Override
    public T findLast(Predicate<T> predicate) {
        AtomicReference<T> maxT = new AtomicReference<>();
        engine.iteration((key, value) -> {
            long id = Codec.decodeKey(key);
            T t = Codec.decode(value, clazz);
            boolean b = t != null && (maxT.get() == null || id > maxT.get().objectId());
            if (b) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                maxT.set(t);
            }
        });
        return maxT.get();
    }


    @Override
    public T findLast() {
        return findLast(null);
    }


    @Override
    public T findOne(long id) {
        byte[] key = Codec.encodeKey(id);
        byte[] value = engine.get(key);
        return (value != null) ? Codec.decode(value, clazz) : null;
    }


    @Override
    public T findOne(Predicate<T> predicate) {
        return engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            return t != null && predicate.test(t) ? t : null;
        });
    }


    @Override
    public T findWithIndex(Consumer<FindOptions> consumer) {
        QuFindOptions options = new QuFindOptions();
        consumer.accept(options);
        long id = indexer.getIndexId(clazz, options.indexName, options.indexValue);
        return findOne(id);
    }


    @Override
    public boolean exist(Consumer<FindOptions> consumer) {
        QuFindOptions options = new QuFindOptions();
        consumer.accept(options);
        return indexer.exist(clazz, options.indexName, options.indexValue);
    }


    @Override
    public void dropIndex(String fieldName) {
        List<T> list = findAll();
        indexer.dropIndex(list, fieldName);
    }


    @Override
    public long count(Predicate<T> predicate) {
        AtomicLong count = new AtomicLong(0);
        engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            if (t != null) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                count.incrementAndGet();
            }
        });
        return count.get();
    }


    @Override
    public long count() {
        return count(null);
    }


    @Override
    public Double sum(String fieldName, Predicate<T> predicate) {
        AtomicDouble sum = new AtomicDouble(0);
        engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            if (t != null) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                sum.addAndGet(new ReflectObject<>(t).getNumberValue(fieldName));
            }
        });
        return sum.get();
    }


    @Override
    public Double sum(String fieldName) {
        return sum(fieldName, null);
    }


    @Override
    public Double average(String fieldName, Predicate<T> predicate) {
        AtomicDouble sum = new AtomicDouble(0);
        AtomicLong count = new AtomicLong(0);
        engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            if (t != null) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                sum.addAndGet(new ReflectObject<>(t).getNumberValue(fieldName));
                count.incrementAndGet();
            }
        });
        return sum.get() / count.get();
    }


    @Override
    public Double average(String fieldName) {
        return average(fieldName, null);
    }


    @Override
    public Double max(String fieldName, Predicate<T> predicate) {
       AtomicReference<Double> max = new AtomicReference<>();
        engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            if (t != null) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                if (max.get() == null) {
                    max.set(new ReflectObject<>(t).getNumberValue(fieldName));
                } else {
                    max.set(Math.max(max.get(), new ReflectObject<>(t).getNumberValue(fieldName)));
                }
            }
        });
        return max.get();
    }


    @Override
    public Double max(String fieldName) {
        return max(fieldName, null);
    }


    @Override
    public Double min(String fieldName, Predicate<T> predicate) {
        AtomicReference<Double> min = new AtomicReference<>();
        engine.iteration((key, value) -> {
            T t = Codec.decode(value, clazz);
            if (t != null) {
                if (predicate != null && !predicate.test(t)) {
                    return;
                }
                if (min.get() == null) {
                    min.set(new ReflectObject<>(t).getNumberValue(fieldName));
                } else {
                    min.set(Math.min(min.get(), new ReflectObject<>(t).getNumberValue(fieldName)));
                }
            }
        });
        return min.get();
    }


    @Override
    public Double min(String fieldName) {
        return min(fieldName, null);
    }

}