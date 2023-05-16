package com.chua.common.support.file.yaml.document;

import com.chua.common.support.file.yaml.YamlConfig;
import com.chua.common.support.file.yaml.emitter.Emitter;
import com.chua.common.support.file.yaml.emitter.EmitterException;
import com.chua.common.support.file.yaml.parser.ScalarEvent;

import java.io.IOException;

/**
 * @author ACHTK
 */
public class YamlEntry {
	
	YamlScalar key;
	AbstractYamlElement value;
	
	public YamlEntry(YamlScalar key, AbstractYamlElement value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(key.toString());
		sb.append(':');
		sb.append(value.toString());
		return sb.toString();
	}
	
	public YamlScalar getKey() {
		return key;
	}
	
	public AbstractYamlElement getValue() {
		return value;
	}
	
	public void setKey(YamlScalar key) {
		this.key = key;
	}
	
	public void setValue(AbstractYamlElement value) {
		this.value = value;
	}

	public void setValue(boolean value) {
		this.value = new YamlScalar(value);
	}
	
	public void setValue(Number value) {
		this.value = new YamlScalar(value);
	}
	
	public void setValue(String value) {
		this.value = new YamlScalar(value);
	}

	public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
		key.emitEvent(emitter, config);
		if(value==null) {
            emitter.emit(new ScalarEvent(null, null, new boolean[] {true, true}, null, config.getQuote().getStyle()));
        } else {
            value.emitEvent(emitter, config);
        }
	}
	
}
