package com.chua.common.support.file.yaml.document;

import com.chua.common.support.file.yaml.YamlConfig;
import com.chua.common.support.file.yaml.emitter.Emitter;
import com.chua.common.support.file.yaml.emitter.EmitterException;
import com.chua.common.support.file.yaml.parser.ScalarEvent;

import java.io.IOException;

/**
 * @author ACHTK
 */
public class YamlScalar extends AbstractYamlElement {

	String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public YamlScalar() {
	}
	
	public YamlScalar(Object value) {
		this.value = String.valueOf(value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(anchor!=null) {
			sb.append('&');
			sb.append(anchor);
			sb.append(' ');
		}
		sb.append(value);
		if(tag!=null) {
			sb.append(" !");
			sb.append(tag);
		}
		return sb.toString();
	}
	
	@Override
	public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
		emitter.emit(new ScalarEvent(anchor, tag, new boolean[] {true, true}, value, config.getQuote().getStyle()));
	}
}
