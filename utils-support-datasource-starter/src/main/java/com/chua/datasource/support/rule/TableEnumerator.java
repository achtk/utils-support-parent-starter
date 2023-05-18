/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chua.datasource.support.rule;

import org.apache.calcite.linq4j.Enumerator;

import java.util.Iterator;
import java.util.List;

/**
 * Enumerator that reads from a Table collection.
 *
 * @author Administrator
 */
public class TableEnumerator implements Enumerator<Object> {

  private final Iterator iterator;

  public TableEnumerator(List rs) {
    this.iterator = rs.iterator();
  }

  @Override
  public Object current() {
    Object[] next = (Object[]) iterator.next();
    return next.length == 1 ? next[0] : next;
  }

  @Override
  public boolean moveNext() {
    return iterator.hasNext();
  }

  @Override
  public void reset() {

  }

  @Override
  public void close() {

  }
}
