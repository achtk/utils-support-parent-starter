package com.chua.common.support.file.yaml.document;

import com.chua.common.support.file.yaml.YamlConfig;
import com.chua.common.support.file.yaml.YamlException;
import com.chua.common.support.file.yaml.emitter.Emitter;
import com.chua.common.support.file.yaml.emitter.EmitterException;
import com.chua.common.support.file.yaml.parser.Event;
import com.chua.common.support.file.yaml.parser.SequenceStartEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class YamlSequence extends AbstractYamlElement implements YamlDocument {

	List<AbstractYamlElement> elements = new LinkedList<AbstractYamlElement>();

	public int size() {
		return elements.size();
	}
	
	public void addElement(AbstractYamlElement element) {
		elements.add(element);
	}
	
	public void deleteElement(int item) throws YamlException {
		elements.remove(item);
	}
	
	public AbstractYamlElement getElement(int item) throws YamlException {
		return elements.get(item);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(anchor!=null) {
			sb.append('&');
			sb.append(anchor);
			sb.append(' ');
		}
		if(tag!=null) {
			sb.append(" !");
			sb.append(tag);
		}
		if(!elements.isEmpty()) {
			sb.append('[');
			for(AbstractYamlElement element : elements) {
				sb.append(element.toString());
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
			sb.append(']');
		}
		return sb.toString();
	}

	
	@Override
	public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
		emitter.emit(new SequenceStartEvent(anchor, tag, tag==null, config.isFlowStyle()));
		for (AbstractYamlElement element : elements) {
			element.emitEvent(emitter, config);
		}
		emitter.emit(Event.SEQUENCE_END);
	}

	public YamlEntry getEntry(String key) throws YamlException {
		throw new YamlException("Can only get entry on mapping!");
	}
	
	public YamlEntry getEntry(int index) throws YamlException {
		throw new YamlException("Can only get entry on mapping!");
	}

	public boolean deleteEntry(String key) throws YamlException {
		throw new YamlException("Can only delete entry on mapping!");
	}

	public void setEntry(String key, boolean value) throws YamlException {
		throw new YamlException("Can only set entry on mapping!");
	}

	public void setEntry(String key, Number value) throws YamlException {
		throw new YamlException("Can only set entry on mapping!");
	}

	public void setEntry(String key, String value) throws YamlException {
		throw new YamlException("Can only set entry on mapping!");
	}

	public void setEntry(String key, AbstractYamlElement value) throws YamlException {
		throw new YamlException("Can only set entry on mapping!");
	}

	public void setElement(int item, boolean value) throws YamlException {
		elements.set(item, new YamlScalar(value));
	}

	public void setElement(int item, Number value) throws YamlException {
		elements.set(item, new YamlScalar(value));
	}

	public void setElement(int item, String value) throws YamlException {
		elements.set(item, new YamlScalar(value));
	}

	public void setElement(int item, AbstractYamlElement element) throws YamlException {
		elements.set(item, element);
	}

	public void addElement(boolean value) throws YamlException {
		elements.add(new YamlScalar(value));
	}

	public void addElement(Number value) throws YamlException {
		elements.add(new YamlScalar(value));
	}

	public void addElement(String value) throws YamlException {
		elements.add(new YamlScalar(value));
	}

	public Iterator<AbstractYamlElement> iterator() {
		return elements.iterator();
	}
}
