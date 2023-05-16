package com.chua.common.support.lang.expression.listener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 监听
 *
 * @author CH
 */
public class DelegateRefreshListener implements RefreshListener {
    final AtomicBoolean change = new AtomicBoolean(false);
    private String content;

    public DelegateRefreshListener(String content) {
        this.content = content;
    }

    @Override
    public boolean isChange() {
        try {
            return change.get();
        } finally {
            change.set(false);
        }
    }

    @Override
    public String getSource() {
        return content;
    }


    @Override
    public void refresh(String source) {
        this.content = source;
        this.change.set(true);
    }
}
