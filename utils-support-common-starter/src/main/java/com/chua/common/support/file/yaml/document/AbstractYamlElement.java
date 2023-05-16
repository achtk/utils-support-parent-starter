package com.chua.common.support.file.yaml.document;

import com.chua.common.support.file.yaml.YamlConfig;
import com.chua.common.support.file.yaml.emitter.Emitter;
import com.chua.common.support.file.yaml.emitter.EmitterException;

import java.io.IOException;

/**
 * @author ACHTK
 */
public abstract class AbstractYamlElement {

    String tag;
    String anchor;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    /**
     * emit
     *
     * @param emitter emit
     * @param config  config
     * @throws EmitterException ex
     * @throws IOException      ex
     */
    public abstract void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException;
}
