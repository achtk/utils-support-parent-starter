package com.chua.common.support.reflection.reflections.serializers;

import com.chua.common.support.reflection.reflections.Reflections;

import java.io.File;
import java.io.InputStream;

/**
 * xml serialization for {@link Reflections} <pre>{@code reflections.save(file, new XmlSerializer())}</pre>
 * <p></p>an example of produced xml:
 * <pre>{@code
 * <Reflections>
 *   <SubTypes>
 *     <entry>
 *       <key>com.chua.common.support.reflections.TestModel$C1</key>
 *       <values>
 *         <value>com.chua.common.support.reflections.TestModel$C2</value>
 *         <value>com.chua.common.support.reflections.TestModel$C3</value>
 *       </values>
 *     </entry>
 *   </SubTypes>
 *   <TypesAnnotated>
 *     <entry>
 *       <key>com.chua.common.support.reflections.TestModel$AC2</key>
 *       <values>
 *         <value>com.chua.common.support.reflections.TestModel$C2</value>
 *         <value>com.chua.common.support.reflections.TestModel$C3</value>
 *       </values>
 *     </entry>
 *   </TypesAnnotated>
 * </Reflections>
 * }</pre>
 *
 * @author Administrator
 */
public class XmlSerializer implements Serializer {

    @Override
    public Reflections read(InputStream inputStream) {
        return null;
    }

    @Override
    public File save(Reflections reflections, String filename) {
        File file = Serializer.prepareFile(filename);
        return file;
    }

}
