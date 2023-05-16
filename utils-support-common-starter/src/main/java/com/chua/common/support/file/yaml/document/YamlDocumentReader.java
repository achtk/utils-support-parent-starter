package com.chua.common.support.file.yaml.document;


import com.chua.common.support.file.yaml.Version;
import com.chua.common.support.file.yaml.YamlException;
import com.chua.common.support.file.yaml.parser.*;
import com.chua.common.support.file.yaml.tokenizer.Tokenizer;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import static com.chua.common.support.file.yaml.parser.EventType.*;


/**
 * @author ACHTK
 */
public class YamlDocumentReader {

    Parser parser;

    public YamlDocumentReader(String yaml) {
        this(new StringReader(yaml));
    }

    public YamlDocumentReader(String yaml, Version version) {
        this(new StringReader(yaml), version);
    }

    public YamlDocumentReader(Reader reader) {
        this(reader, null);
    }

    public YamlDocumentReader(Reader reader, Version version) {
        if (version == null)
            version = Version.DEFAULT_VERSION;
        parser = new Parser(reader, version);
    }

    public YamlDocument read() throws YamlException {
        return read(YamlDocument.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T read(Class<T> type) throws YamlException {
        try {
            while (true) {
                Event event = parser.peekNextEvent();
                if (event == null)
                    return null;
                switch (event.type) {
                    case STREAM_START:
                        parser.getNextEvent(); // consume it
                        break;
                    case STREAM_END:
                        parser.getNextEvent(); // consume it
                        return null;
                    case DOCUMENT_START:
                        parser.getNextEvent(); // consume it
                        return (T) readDocument();
                    default:
                        throw new IllegalStateException();
                }
            }
        } catch (Parser.ParserException ex) {
            throw new YamlException("Error parsing YAML.", ex);
        } catch (Tokenizer.TokenizerException ex) {
            throw new YamlException("Error tokenizing YAML.", ex);
        }
    }

    public <T> Iterator<T> readAll(final Class<T> type) {
        Iterator<T> iterator = new Iterator<T>() {

            public boolean hasNext() {
                Event event = parser.peekNextEvent();
                return event != null && event.type != STREAM_END;
            }

            public T next() {
                try {
                    return read(type);
                } catch (YamlException e) {
                    throw new RuntimeException("Iterative reading documents exception", e);
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        return iterator;
    }

    private AbstractYamlElement readDocument() {
        AbstractYamlElement yamlElement = null;
        Event event = parser.peekNextEvent();
        switch (event.type) {
            case SCALAR:
                yamlElement = readScalar();
                break;
            case ALIAS:
                yamlElement = readAlias();
                break;
            case MAPPING_START:
                yamlElement = readMapping();
                break;
            case SEQUENCE_START:
                yamlElement = readSequence();
                break;
            default:
                throw new IllegalStateException();
        }
        parser.getNextEvent();
        return yamlElement;
    }

    private YamlMapping readMapping() {
        Event event = parser.getNextEvent();
        if (event.type != MAPPING_START) {
			throw new IllegalStateException();
		}
        YamlMapping element = new YamlMapping();
        MappingStartEvent mapping = (MappingStartEvent) event;
        element.setTag(mapping.tag);
        element.setAnchor(mapping.anchor);
        readMappingElements(element);
        return element;
    }

    private void readMappingElements(YamlMapping mapping) {
        while (true) {
            Event event = parser.peekNextEvent();
            if (event.type == MAPPING_END) {
                parser.getNextEvent();
                return;
            } else {
                YamlEntry entry = readEntry();
                mapping.addEntry(entry);
            }
        }
    }

    private YamlEntry readEntry() {
        YamlScalar scalar = readScalar();
        AbstractYamlElement value = readValue();
        return new YamlEntry(scalar, value);
    }

    private AbstractYamlElement readValue() {
        Event event = parser.peekNextEvent();
        switch (event.type) {
            case SCALAR:
                return readScalar();
            case ALIAS:
                return readAlias();
            case MAPPING_START:
                return readMapping();
            case SEQUENCE_START:
                return readSequence();
            default:
                throw new IllegalStateException();
        }
    }

    private YamlAlias readAlias() {
        Event event = parser.getNextEvent();
        if (event.type != ALIAS) {
			throw new IllegalStateException();
		}
        YamlAlias element = new YamlAlias();
        AliasEvent alias = (AliasEvent) event;
        element.setAnchor(alias.anchor);
        return element;
    }

    private YamlSequence readSequence() {
        Event event = parser.getNextEvent();
        if (event.type != SEQUENCE_START) {
            throw new IllegalStateException();
        }
        YamlSequence element = new YamlSequence();
        SequenceStartEvent sequence = (SequenceStartEvent) event;
        element.setTag(sequence.tag);
        element.setAnchor(sequence.anchor);
        readSequenceElements(element);
        return element;
    }

    private void readSequenceElements(YamlSequence sequence) {
        while (true) {
            Event event = parser.peekNextEvent();
            if (event.type == SEQUENCE_END) {
                parser.getNextEvent();
                return;
            } else {
                AbstractYamlElement element = readValue();
                sequence.addElement(element);
            }
        }
    }

    private YamlScalar readScalar() {
        Event event = parser.getNextEvent();
        if (event.type != SCALAR) {
			throw new IllegalStateException();
		}
        ScalarEvent scalar = (ScalarEvent) event;
        YamlScalar element = new YamlScalar();
        element.setTag(scalar.tag);
        element.setAnchor(scalar.anchor);
        element.setValue(scalar.value);
        return element;
    }

}
