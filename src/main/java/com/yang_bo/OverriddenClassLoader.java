package com.yang_bo;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author 杨博 (Yang Bo)
 */
public class OverriddenClassLoader extends URLClassLoader {
    public OverriddenClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public OverriddenClassLoader(URL[] urls) {
        super(urls);
    }

    public OverriddenClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    protected Collection<Pattern> getOverriddenClasses() {
        return Collections.singletonList(Pattern.compile(".*"));
    }

    protected Collection<Pattern> getExcludeClasses() {
        return Collections.emptyList();
    }

    protected Collection<Pattern> getOverriddenResources() {
        return Collections.singletonList(Pattern.compile(".*"));
    }

    protected Collection<Pattern> getExcludeResources() {
        return Collections.emptyList();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        if (getOverriddenClasses().stream().anyMatch(pattern -> pattern.matcher(name).find()) &&
                getExcludeClasses().stream().noneMatch(pattern -> pattern.matcher(name).find())) {
            synchronized (getClassLoadingLock(name)) {
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    c = findClass(name);
                }
                if (c != null && resolve) {
                    resolveClass(c);
                }
                return c;
            }
        } else {
            return super.loadClass(name, resolve);
        }
    }

    @Override
    public URL getResource(String name) {
        if (getOverriddenResources().stream().anyMatch(pattern -> pattern.matcher(name).find()) &&
                getExcludeResources().stream().noneMatch(pattern -> pattern.matcher(name).find())) {
            return findResource(name);
        } else {
            return super.getResource(name);
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (getOverriddenResources().stream().anyMatch(pattern -> pattern.matcher(name).find()) &&
                getExcludeResources().stream().noneMatch(pattern -> pattern.matcher(name).find())) {
            return findResources(name);
        } else {
            return super.getResources(name);
        }
    }

}
