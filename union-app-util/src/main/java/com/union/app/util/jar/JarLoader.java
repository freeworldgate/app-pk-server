package com.union.app.util.jar;


import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader {


    public static Map<String,Class<?>> loadJars(Collection<File> jars) throws IOException, ClassNotFoundException {

        URL[] fileUrls = fileToURLs(jars);
        URLClassLoader urlClassLoader = new URLClassLoader(fileUrls,ClassUtils.getDefaultClassLoader());
        Map<String,Class<?>> classMap = parseApis(jars,urlClassLoader);

        return classMap;
    }

    private static Map<String,Class<?>> parseApis(Collection<File> jars,URLClassLoader urlClassLoader) throws IOException, ClassNotFoundException {
        List<JarEntry> jarEntries = parseJars(jars);
        Map<String,Class<?>> classMap = new HashMap<>();
        for(JarEntry jarEntry:jarEntries)
        {
            String entryName = findClassEntry(jarEntry);
            if(entryName != null)
            {
                classMap.put(entryName,urlClassLoader.loadClass(entryName));
            }


        }

        return classMap;
    }

    private static String findClassEntry(JarEntry jarEntry)
    {
        String entryName = jarEntry.getName();
        if(entryName.endsWith(".class"))
        {
            return entryName.replace("/",".").replace(".class","");
        }
        return null;
    }

    private static List<JarEntry> parseJars(Collection<File> jars) throws IOException {
        List<JarEntry> jarEntries = new ArrayList<>();

        return jarEntries;
    }

    private static URL[] fileToURLs(Collection<File> jars) throws MalformedURLException {
        List<URL> uris = new ArrayList<URL>();
        for(File file:jars)
        {
            URL uri = file.toURI().toURL();
            uris.add(uri);
        }
        URL[] uriArr = new URL[uris.size()];
        uriArr = uris.toArray(uriArr);

        return uriArr;
    }
}
