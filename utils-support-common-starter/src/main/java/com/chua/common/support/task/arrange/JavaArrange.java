package com.chua.common.support.task.arrange;

import com.chua.common.support.task.arrange.async.executor.Async;
import com.chua.common.support.view.view.TreeView;
import com.chua.common.support.view.view.TreeViewNode;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * 编排
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
public class JavaArrange implements Arrange {

    final Map<String, Worker> worker = new LinkedHashMap<>();
    final Map<String, List<Worker>> after = new LinkedHashMap<>();
    final Map<String, List<String>> after2 = new LinkedHashMap<>();

    @Override
    public <T, V> Arrange addWorker(Worker<T, V> worker) {
        this.worker.put(worker.getId(), worker);
        return this;
    }

    @Override
    public <T, V> Arrange addAfter(String name, Worker<T, V> worker) {
        this.after.computeIfAbsent(name, it -> new LinkedList<>()).add(worker);
        this.worker.put(worker.getId(), worker);
        return this;
    }

    @Override
    public <T, V> Arrange addAfter(String name, String name2) {
        this.after2.computeIfAbsent(name, it -> new LinkedList<>()).add(name2);
        return this;
    }

    @Override
    public void start(int timeout) throws ExecutionException, InterruptedException {
        List<Worker> noDepends = analysisNoDepends();
        Async.beginWork(timeout, noDepends.toArray(new Worker[0]));

        Async.shutDown();
    }

    private List<Worker> analysisNoDepends() {
        List<Worker> rs = new LinkedList<>();
        doAnalysisNameWorker();
        doAnalysisNoDepends(rs);
        return rs;
    }

    private void doAnalysisNameWorker() {
        for (Map.Entry<String, List<String>> entry : after2.entrySet()) {
            List<String> value = entry.getValue();
            for (String s : value) {
                if (!worker.containsKey(s)) {
                    continue;
                }
                addAfter(entry.getKey(), worker.get(s));
            }
        }
    }

    private void doAnalysisNoDepends(List<Worker> rs) {
        Set<Worker> tpl = new LinkedHashSet<>();
        tpl.addAll(worker.values());
        for (List<Worker> value : after.values()) {
            tpl.addAll(value);
        }
        doAnalysisNoDepends(tpl, worker, after);
        rs.addAll(tpl);
    }

    private void doAnalysisNoDepends(Set<Worker> tpl, Map<String, Worker> worker, Map<String, List<Worker>> after) {
        for (List<Worker> value : after.values()) {
            tpl.removeAll(value);
        }

        for (Map.Entry<String, List<Worker>> entry : after.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split(",");
            for (String s : split) {
                doAnalysisOne(worker, s, entry.getValue());
            }
        }
    }

    private void doAnalysisOne(Map<String, Worker> worker, String s, List<Worker> value) {
        Worker worker1 = worker.get(s);
        if (null != worker1) {
            worker1.setNextWorker(value);
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public String print() {
        List<Worker> noDepends = analysisNoDepends();
        if (noDepends.size() == 1) {
            TreeView treeView = new TreeView(true, noDepends.get(0));
            return treeView.draw();
        }

        TreeViewNode treeViewNode = TreeViewNode.newBuilder("root");
        for (Worker noDepend : noDepends) {
            treeViewNode.addChildren(noDepend);
        }
        TreeView treeView = new TreeView(true, treeViewNode);
        return treeView.draw();
    }
}
