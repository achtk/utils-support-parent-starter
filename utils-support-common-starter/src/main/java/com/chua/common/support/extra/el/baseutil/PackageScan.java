package com.chua.common.support.extra.el.baseutil;

import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 包扫描类，可以扫描某个路径下所有的class
 *
 * @author admin
 */
public class PackageScan
{
    /**
     * 根据给定的包名，返回包下面所有的类的全限定名 支持过滤语法，过滤语法以：开始。 规则有：
     * （1）以“in~”开头，代表必须包含后面的包名.返回的字符串数组中的字符串均包含后面的包名
     * （2）以“out~”开头，代表不包含后面的包名。返回的字符串数组中的字符串均不包含后面的包名
     *
     * @param packageName
     * @return
     */
    public static String[] scan(String packageName)
    {
        String filterNames = null;
        if (packageName.contains(":"))
        {
            filterNames = packageName.split(":")[1];
            packageName = packageName.split(":")[0];
        }
        List<String> classNames   = new LinkedList<String>();
        ClassLoader  loader       = Thread.currentThread().getContextClassLoader();
        String       resourceName = packageName.replaceAll("\\.", "/");
        try
        {
            Enumeration<URL> urls = loader.getResources(resourceName);
            while (urls.hasMoreElements())
            {
                URL url = urls.nextElement();
                if (url.getProtocol().contains("file"))
                {
                    try
                    {
                        File urlFile = new File(url.toURI());
                        packageName = packageName.substring(0, packageName.lastIndexOf(".") + 1);
                        findClassNamesByFile(packageName, urlFile, classNames);
                    }
                    catch (URISyntaxException e)
                    {
                        ReflectUtil.throwException(e);
                    }
                }
                else if (url.getProtocol().contains("jar"))
                {
                    getClassNamesByJar(url, resourceName, classNames);
                }
            }
        }
        catch (IOException e1)
        {
            ReflectUtil.throwException(e1);
        }
        doFilter(filterNames, classNames);
        return classNames.toArray(new String[classNames.size()]);
    }

    /**
     * 将url所表示的jar路径的jar读取，并且将其中的class文件放入到list中，返回list
     *
     * @param url
     * @param packageName
     * @return
     * @throws IOException
     */
    private static void getClassNamesByJar(URL url, String packageName, List<String> classNames)
    {
        JarFile jarFile = null;
        try
        {
            // 获取正确并且完成的jar路径的url表示
            JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
            jarFile = urlConnection.getJarFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException("url地址：'" + url.toString() + "'不正确", e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements())
        {
            JarEntry jarEntry  = entries.nextElement();
            String   entryName = jarEntry.getName();
            // 将符合条件的class文件的全限定名加入到list中
            if (entryName.endsWith(".class") && entryName.startsWith(packageName))
            {
                String className = entryName.substring(0, entryName.indexOf(".class"));
                className = className.replaceAll("/", ".");
                classNames.add(className);
            }
        }
    }

    /**
     * 如果packageFile是class文件，则将该类的全限定名加入到list中。如果是文件夹就遍历，然后对每一个子文件或者文件夹重复该过程
     *
     * @param packageName 当前的前缀包名
     * @param packageFile 当前的文件
     */
    private static void findClassNamesByFile(String packageName, File packageFile, List<String> classNames)
    {
        if (packageFile.isFile())
        {
            String className = packageName + packageFile.getName().replace(".class", "");
            className = className.replaceAll("/", ".");
            classNames.add(className);
        }
        else
        {
            File[] files         = packageFile.listFiles();
            String tmPackageName = packageName + packageFile.getName() + ".";
            for (File f : files)
            {
                findClassNamesByFile(tmPackageName, f, classNames);
            }
        }
    }

    /**
     * 进行过滤。过滤规则参照调用方法。 字符串匹配规则参照StringUtil的Match方法
     *
     * @param filterNames
     * @param classNames
     */
    private static void doFilter(String filterNames, List<String> classNames)
    {
        if (filterNames == null)
        {
            return;
        }
        if (filterNames.startsWith("in~"))
        {
            String[] filters = filterNames.substring(3).split(",");
            for (String filter : filters)
            {
                inFilter(filter, classNames);
            }
        }
        else if (filterNames.startsWith("out~"))
        {
            String[] filters = filterNames.substring(4).split(",");
            for (String filter : filters)
            {
                outFilter(filter, classNames);
            }
        }
    }

    private static void inFilter(String rule, List<String> classNames)
    {
        Iterator<String> iterator = classNames.iterator();
        while (iterator.hasNext())
        {
            String value = iterator.next();
            if (StringUtil.match(value, rule) == false)
            {
                iterator.remove();
            }
        }
    }

    private static void outFilter(String rule, List<String> classNames)
    {
        Iterator<String> iterator = classNames.iterator();
        while (iterator.hasNext())
        {
            String value = iterator.next();
            if (StringUtil.match(value, rule))
            {
                iterator.remove();
            }
        }
    }
}
