package com.union.app.util.jar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtil
{


    private static final String JAR_FILE_SUFFIX1 = "jar";
    private static final String JAR_FILE_SUFFIX2 = "JAR";
    private static final String CLASS_PATH_SPLITE = "/";
    private static final String CLASS_FILE_SUFFIX = ".class";
    private static final String EMPTY_STRING = "";

    public static Map<String,Class<?>> loadJars(File jarDir) throws IOException, ClassNotFoundException {
        Collection<File> jars = FileUtils.listFiles(jarDir,new String[]{JAR_FILE_SUFFIX1,JAR_FILE_SUFFIX2},Boolean.TRUE);
        URL[] jarUrls = FileUtils.toURLs(jars.toArray(new File[jars.size()]));
        URLClassLoader urlClassLoader = new URLClassLoader(jarUrls,JarUtil.class.getClassLoader());
        Collection<JarEntry> jarEntries = analyserJarEntry(jars);
        Map<String,Class<?>> classMap = loadClass(jarEntries,urlClassLoader);
        return classMap;
    }

    private static Map<String,Class<?>> loadClass(Collection<JarEntry> jarEntries, URLClassLoader urlClassLoader) throws ClassNotFoundException {
        Map<String,Class<?>> classMap = new HashMap<>();
        for(JarEntry jarEntry:jarEntries)
        {
            String className = jarEntry.getName().replace(CLASS_PATH_SPLITE,EMPTY_STRING).replace(CLASS_FILE_SUFFIX,EMPTY_STRING);
            Class<?> clazz = urlClassLoader.loadClass(className);
            classMap.put(className,clazz);
        }
        return classMap;
    }

    private static Collection<JarEntry> analyserJarEntry(Collection<File> jars) throws IOException {
        Collection<JarEntry> jarEntries = new ArrayList<>();
        for(File file:jars)
        {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements())
            {
                jarEntries.add(entryEnumeration.nextElement());
            }
        }
        return jarEntries;
    }


}
