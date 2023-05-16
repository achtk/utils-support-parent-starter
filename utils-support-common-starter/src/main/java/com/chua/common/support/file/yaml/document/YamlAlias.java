package com.chua.common.support.file.yaml.document;

import com.chua.common.support.file.yaml.YamlConfig;
import com.chua.common.support.file.yaml.emitter.Emitter;
import com.chua.common.support.file.yaml.emitter.EmitterException;
import com.chua.common.support.file.yaml.parser.AliasEvent;

import java.io.IOException;
/**
 * @author ACHTK
 */
public class YamlAlias extends AbstractYamlElement {

	@Override
	public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
		emitter.emit(new AliasEvent(anchor));
	}
	
	@Override
	public String toString() {
		return "*" + anchor;
	}
}
