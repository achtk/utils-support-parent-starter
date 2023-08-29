package com.chua.common.support.lang.formatter.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 基础类
 * @author CH
 */
public class JsLikeList<T> implements Iterable<T> {

  private List<T> tList;

  public JsLikeList(List<T> tList) {
    this.tList = tList == null ? Collections.emptyList() : new ArrayList<>(tList);
  }

  public List<T> toList() {
    return this.tList;
  }

  public <R> JsLikeList<R> map(Function<T, R> mapper) {
    return new JsLikeList<>(this.tList.stream().map(mapper).collect(Collectors.toList()));
  }

  public String join(CharSequence delimiter) {
    return this.tList.stream()
        .map(Optional::ofNullable)
        .map(x -> x.map(String::valueOf).orElse(""))
        .collect(Collectors.joining(delimiter));
  }

  public JsLikeList<T> with(List<T> other) {
    List<T> list = new ArrayList<>();
    list.addAll(this.toList());
    list.addAll(other);
    return new JsLikeList<>(list);
  }

  public String join() {
    return join(",");
  }

  public boolean isEmpty() {
    return this.tList == null || this.tList.isEmpty();
  }

  public T get(int index) {
    if (index < 0) {
      return null;
    }
    if (tList.size() <= index) {
      return null;
    }
    return this.tList.get(index);
  }

  @Override
  public Iterator<T> iterator() {
    return this.tList.iterator();
  }

  public Stream<T> stream() {
    return this.tList.stream();
  }

  public int size() {
    return this.tList.size();
  }
}
