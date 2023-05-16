package com.chua.common.support.file.yaml.parser;

import com.chua.common.support.file.yaml.Version;

import java.util.Map;

/** @author Nathan Sweet
 * @author Ola Bini */
public class DocumentStartEvent extends Event {
	public final boolean isExplicit;
	public final Version version;
	public final Map<String, String> tags;

	public DocumentStartEvent (boolean explicit, Version version, Map<String, String> tags) {
		super(EventType.DOCUMENT_START);
		this.isExplicit = explicit;
		this.version = version;
		this.tags = tags;
	}

	public String toString () {
		return "<" + type + " explicit='" + isExplicit + "' version='" + version + "' tags='" + tags + "'>";
	}
}
