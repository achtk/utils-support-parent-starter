package com.chua.common.support.lang.compile;

import com.chua.common.support.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.*;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;

import static com.chua.common.support.constant.CommonConstant.JAR_URL_SEPARATOR;

/**
 * jdk 编译器
 *
 * @author CHTK
 */
@Slf4j
public class JdkCompiler implements Compiler {
    @Override
    public Class<?> doCompile(String name, String source) throws Throwable {
        DynamicCompiler dynamicCompiler = new DynamicCompiler(Thread.currentThread().getContextClassLoader());
        dynamicCompiler.addSource(name, source);
        Map<String, Class<?>> build = dynamicCompiler.build();
        if (build.isEmpty()) {
            return null;
        }
        return build.values().iterator().next();
    }

    /**
     * 累计在其
     */
    static final class DynamicClassLoader extends ClassLoader {
        private final Map<String, MemoryByteCode> byteCodes = new HashMap<String, MemoryByteCode>();

        public DynamicClassLoader(ClassLoader classLoader) {
            super(classLoader);
        }

        public Map<String, byte[]> getByteCodes() {
            Map<String, byte[]> result = new HashMap<String, byte[]>(byteCodes.size());
            for (Map.Entry<String, MemoryByteCode> entry : byteCodes.entrySet()) {
                result.put(entry.getKey(), entry.getValue().getByteCode());
            }
            return result;
        }

        public Map<String, Class<?>> getClasses() throws ClassNotFoundException {
            Map<String, Class<?>> classes = new HashMap<String, Class<?>>(1 << 4);
            for (MemoryByteCode byteCode : byteCodes.values()) {
                classes.put(byteCode.getClassName(), findClass(byteCode.getClassName()));
            }
            return classes;
        }

        public void registerCompiledSource(MemoryByteCode byteCode) {
            byteCodes.put(byteCode.getClassName(), byteCode);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            MemoryByteCode byteCode = byteCodes.get(name);
            if (byteCode == null) {
                return super.findClass(name);
            }

            return super.defineClass(name, byteCode.getByteCode(), 0, byteCode.getByteCode().length);
        }
    }

    /**
     * 文件管理器
     */
    static final class DynamicJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private static final String[] SUPER_LOCATION_NAMES = {StandardLocation.PLATFORM_CLASS_PATH.name(),
                /** JPMS StandardLocation.SYSTEM_MODULES **/
                "SYSTEM_MODULES"};
        private final PackageInternalsFinder finder;

        private final DynamicClassLoader classLoader;
        private final List<MemoryByteCode> byteCodes = new ArrayList<MemoryByteCode>();

        public DynamicJavaFileManager(JavaFileManager fileManager, DynamicClassLoader classLoader) {
            super(fileManager);
            this.classLoader = classLoader;

            finder = new PackageInternalsFinder(classLoader);
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return classLoader;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                   JavaFileObject.Kind kind, FileObject sibling) throws IOException {

            for (MemoryByteCode byteCode : byteCodes) {
                if (byteCode.getClassName().equals(className)) {
                    return byteCode;
                }
            }

            MemoryByteCode innerClass = new MemoryByteCode(className);
            byteCodes.add(innerClass);
            classLoader.registerCompiledSource(innerClass);
            return innerClass;

        }

        @Override
        public String inferBinaryName(Location location, JavaFileObject file) {
            if (file instanceof CustomJavaFileObject) {
                return ((CustomJavaFileObject) file).binaryName();
            } else {
                /**
                 * if it's not CustomJavaFileObject, then it's coming from standard file manager
                 * - let it handle the file
                 */
                return super.inferBinaryName(location, file);
            }
        }

        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds,
                                             boolean recurse) throws IOException {
            if (location instanceof StandardLocation) {
                String locationName = ((StandardLocation) location).name();
                for (String name : SUPER_LOCATION_NAMES) {
                    if (name.equals(locationName)) {
                        return super.list(location, packageName, kinds, recurse);
                    }
                }
            }

            // merge JavaFileObjects from specified ClassLoader
            if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
                return new IterableJoin<JavaFileObject>(super.list(location, packageName, kinds, recurse),
                        finder.find(packageName));
            }

            return super.list(location, packageName, kinds, recurse);
        }

        static class IterableJoin<T> implements Iterable<T> {
            private final Iterable<T> first, next;

            public IterableJoin(Iterable<T> first, Iterable<T> next) {
                this.first = first;
                this.next = next;
            }

            @Override
            public Iterator<T> iterator() {
                return new IteratorJoin<T>(first.iterator(), next.iterator());
            }
        }

        static class IteratorJoin<T> implements Iterator<T> {
            private final Iterator<T> first, next;

            public IteratorJoin(Iterator<T> first, Iterator<T> next) {
                this.first = first;
                this.next = next;
            }

            @Override
            public boolean hasNext() {
                return first.hasNext() || next.hasNext();
            }

            @Override
            public T next() {
                if (first.hasNext()) {
                    return first.next();
                }
                return next.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        }
    }

    /**
     * 迭代器
     *
     * @param <T>
     */
    static class IteratorJoin<T> implements Iterator<T> {
        private final Iterator<T> first, next;

        public IteratorJoin(Iterator<T> first, Iterator<T> next) {
            this.first = first;
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return first.hasNext() || next.hasNext();
        }

        @Override
        public T next() {
            if (first.hasNext()) {
                return first.next();
            }
            return next.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    /**
     * 字节编译器
     */
    static final class MemoryByteCode extends SimpleJavaFileObject {
        private static final char PKG_SEPARATOR = '.';
        private static final char DIR_SEPARATOR = '/';
        private static final String CLASS_FILE_SUFFIX = ".class";

        private ByteArrayOutputStream byteArrayOutputStream;

        public MemoryByteCode(String className) {
            super(URI.create("byte:///" + className.replace(PKG_SEPARATOR, DIR_SEPARATOR)
                    + Kind.CLASS.extension), Kind.CLASS);
        }

        public MemoryByteCode(String className, ByteArrayOutputStream byteArrayOutputStream)
                throws URISyntaxException {
            this(className);
            this.byteArrayOutputStream = byteArrayOutputStream;
        }

        public byte[] getByteCode() {
            return byteArrayOutputStream.toByteArray();
        }

        public String getClassName() {
            String className = getName();
            className = className.replace(DIR_SEPARATOR, PKG_SEPARATOR);
            className = className.substring(1, className.indexOf(CLASS_FILE_SUFFIX));
            return className;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            if (byteArrayOutputStream == null) {
                byteArrayOutputStream = new ByteArrayOutputStream();
            }
            return byteArrayOutputStream;
        }

    }

    /**
     * 封装内部查找器
     */
    static final class PackageInternalsFinder {
        private static final String CLASS_FILE_EXTENSION = ".class";
        private final ClassLoader classLoader;

        public PackageInternalsFinder(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        public List<JavaFileObject> find(String packageName) throws IOException {
            String javaPackageName = packageName.replaceAll("\\.", "/");

            List<JavaFileObject> result = new ArrayList<JavaFileObject>();

            Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
            while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
                URL element = urlEnumeration.nextElement();
                result.addAll(listUnder(packageName, element));
            }

            return result;
        }

        private Collection<JavaFileObject> listUnder(String packageName, URL url) {
            File directory = new File(url.getFile());
            if (directory.isDirectory()) { // browse local .class files - useful for local execution
                return processDir(packageName, directory);
            } else { // browse a jar file
                return processJar(url);
            } // maybe there can be something else for more involved class loaders
        }

        private List<JavaFileObject> processJar(URL url) {
            List<JavaFileObject> result = new ArrayList<JavaFileObject>();
            try {
                String jarUri = url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf(JAR_URL_SEPARATOR));

                JarURLConnection jarConn = (JarURLConnection) url.openConnection();
                String rootEntryName = jarConn.getEntryName();
                int rootEnd = rootEntryName.length() + 1;

                Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
                while (entryEnum.hasMoreElements()) {
                    JarEntry jarEntry = entryEnum.nextElement();
                    String name = jarEntry.getName();
                    if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS_FILE_EXTENSION)) {
                        URI uri = URI.create(jarUri + JAR_URL_SEPARATOR + name);
                        String binaryName = name.replaceAll("/", ".");
                        binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                        result.add(new CustomJavaFileObject(binaryName, uri));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Wasn't able to open " + url + " as a jar file", e);
            }
            return result;
        }

        private List<JavaFileObject> processDir(String packageName, File directory) {
            List<JavaFileObject> result = new ArrayList<JavaFileObject>();

            File[] childFiles = directory.listFiles();
            for (File childFile : childFiles) {
                if (childFile.isFile()) {
                    // We only want the .class files.
                    if (childFile.getName().endsWith(CLASS_FILE_EXTENSION)) {
                        String binaryName = packageName + "." + childFile.getName();
                        binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                        result.add(new CustomJavaFileObject(binaryName, childFile.toURI()));
                    }
                }
            }

            return result;
        }
    }

    /**
     * 动态编译
     */
    final class DynamicCompiler {
        private final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        private final StandardJavaFileManager standardFileManager;
        private final List<String> options = new ArrayList<>();
        private final DynamicClassLoader dynamicClassLoader;

        private final Collection<JavaFileObject> compilationUnits = new ArrayList<>();
        private final List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<>();
        private final List<Diagnostic<? extends JavaFileObject>> warnings = new ArrayList<>();

        private final Writer writer;

        public DynamicCompiler(ClassLoader classLoader) {
            this(classLoader, null);
        }

        public DynamicCompiler(ClassLoader classLoader, Writer writer) {
            standardFileManager = javaCompiler.getStandardFileManager(null, null, null);

            options.add("-Xlint:unchecked");
            dynamicClassLoader = new DynamicClassLoader(classLoader);
            this.writer = writer;
        }

        public void addSource(String className, String source) {
            addSource(new StringSource(className, source));
        }

        public void addSource(JavaFileObject javaFileObject) {
            compilationUnits.add(javaFileObject);
        }

        public Map<String, Class<?>> build() {

            errors.clear();
            warnings.clear();

            JavaFileManager fileManager = new DynamicJavaFileManager(standardFileManager, dynamicClassLoader);

            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, collector, options, null,
                    compilationUnits);

            try {

                if (!compilationUnits.isEmpty()) {
                    boolean result = task.call();

                    if (!result || collector.getDiagnostics().size() > 0) {

                        for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                            switch (diagnostic.getKind()) {
                                case NOTE:
                                case MANDATORY_WARNING:
                                case WARNING:
                                    warnings.add(diagnostic);
                                    break;
                                case OTHER:
                                case ERROR:
                                default:
                                    errors.add(diagnostic);
                                    break;
                            }

                        }

                        if (!errors.isEmpty()) {
                            throw new IllegalStateException("Compilation Error" + errors);
                        }
                    }
                }

                return dynamicClassLoader.getClasses();
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            } finally {
                compilationUnits.clear();

            }

        }

        public Map<String, byte[]> buildByteCodes() {

            errors.clear();
            warnings.clear();

            JavaFileManager fileManager = new DynamicJavaFileManager(standardFileManager, dynamicClassLoader);

            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, collector, options, null,
                    compilationUnits);

            try {

                if (!compilationUnits.isEmpty()) {
                    boolean result = task.call();

                    if (!result || collector.getDiagnostics().size() > 0) {

                        for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                            switch (diagnostic.getKind()) {
                                case NOTE:
                                case MANDATORY_WARNING:
                                case WARNING:
                                    warnings.add(diagnostic);
                                    break;
                                case OTHER:
                                case ERROR:
                                default:
                                    errors.add(diagnostic);
                                    break;
                            }

                        }

                        if (!errors.isEmpty()) {
                            throw new IllegalStateException("Compilation Error" + errors);
                        }
                    }
                }

                return dynamicClassLoader.getByteCodes();
            } catch (ClassFormatError e) {
                throw new IllegalStateException(e);
            } finally {
                compilationUnits.clear();

            }

        }

        public ClassLoader getClassLoader() {
            return dynamicClassLoader;
        }

        public List<String> getErrors() {
            return diagnosticToString(errors);
        }

        public List<String> getWarnings() {
            return diagnosticToString(warnings);
        }

        private List<String> diagnosticToString(List<Diagnostic<? extends JavaFileObject>> diagnostics) {

            List<String> diagnosticMessages = new ArrayList<String>();

            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                diagnosticMessages.add(
                        "line: " + diagnostic.getLineNumber() + ", message: " + diagnostic.getMessage(Locale.US));
            }

            return diagnosticMessages;

        }
    }

    /**
     * 字节编译器
     */
    public static class StringSource extends SimpleJavaFileObject {
        private final String contents;

        public StringSource(String className, String contents) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return contents;
        }
    }

    /**
     * 文件对象
     */
    public static class CustomJavaFileObject implements JavaFileObject {
        private final String binaryName;
        private final URI uri;
        private final String name;

        public CustomJavaFileObject(String binaryName, URI uri) {
            this.uri = uri;
            this.binaryName = binaryName;
            this.name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath();
        }

        @Override
        public URI toUri() {
            return this.uri;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return this.uri.toURL().openStream();
        }

        @Override
        public OutputStream openOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Writer openWriter() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastModified() {
            return 0L;
        }

        @Override
        public boolean delete() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Kind getKind() {
            return Kind.CLASS;
        }

        @Override
        public boolean isNameCompatible(String simpleName, Kind kind) {
            String baseName = simpleName + kind.extension;
            return kind.equals(this.getKind()) && (baseName.equals(this.getName()) || this.getName().endsWith("/" + baseName));
        }

        @Override
        public NestingKind getNestingKind() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Modifier getAccessLevel() {
            throw new UnsupportedOperationException();
        }

        public String binaryName() {
            return this.binaryName;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET + this.toUri() + CommonConstant.SYMBOL_RIGHT_SQUARE_BRACKET;
        }
    }
}
