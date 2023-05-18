/**
 * Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.chua.agent.support.reflectasm;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.WeakHashMap;

class AccessClassLoader extends ClassLoader {
    static private final WeakHashMap<ClassLoader, WeakReference<AccessClassLoader>> ACCESS_CLASS_LOADERS = new WeakHashMap();

    static private final ClassLoader SELF_CONTEXT_PARENT_CLASS_LOADER = getParentClassLoader(AccessClassLoader.class);
    static private volatile AccessClassLoader selfContextAccessClassLoader = new AccessClassLoader(SELF_CONTEXT_PARENT_CLASS_LOADER);

    static private volatile Method defineClassMethod;

    private final HashSet<String> localClassNames = new HashSet();

    private AccessClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Returns null if the access class has not yet been defined.
     */
    Class loadAccessClass(String name) {
        // No need to check the parent class loader if the access class hasn't been defined yet.
        if (localClassNames.contains(name)) {
            try {
                return loadClass(name, false);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    Class defineAccessClass(String name, byte[] bytes) throws ClassFormatError {
        localClassNames.add(name);
        return defineClass(name, bytes);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // These classes come from the classloader that loaded AccessClassLoader.
        if (name.equals(FieldAccess.class.getName())) {
            return FieldAccess.class;
        }
        if (name.equals(MethodAccess.class.getName())) {
            return MethodAccess.class;
        }
        if (name.equals(ConstructorAccess.class.getName())) {
            return ConstructorAccess.class;
        }
        if (name.equals(PublicConstructorAccess.class.getName())) {
            return PublicConstructorAccess.class;
        }
        // All other classes come from the classloader that loaded the type we are accessing.
        return super.loadClass(name, resolve);
    }

    Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
        try {
            // Attempt to load the access class in the same loader, which makes protected and default access members accessible.
            return (Class<?>) getDefineClassMethod().invoke(getParent(),
                    new Object[]{name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length), getClass().getProtectionDomain()});
        } catch (Exception ignored) {
            // continue with the definition in the current loader (won't have access to protected and package-protected members)
        }
        return defineClass(name, bytes, 0, bytes.length, getClass().getProtectionDomain());
    }

    static boolean areInSameRuntimeClassLoader(Class type1, Class type2) {
        if (type1.getPackage() != type2.getPackage()) {
            return false;
        }
        ClassLoader loader1 = type1.getClassLoader();
        ClassLoader loader2 = type2.getClassLoader();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (loader1 == null) {
            return (loader2 == null || loader2 == systemClassLoader);
        }
        if (loader2 == null) {
            return loader1 == systemClassLoader;
        }
        return loader1 == loader2;
    }

    static private ClassLoader getParentClassLoader(Class type) {
        ClassLoader parent = type.getClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return parent;
    }

    static private Method getDefineClassMethod() throws Exception {
        if (defineClassMethod == null) {
            synchronized (ACCESS_CLASS_LOADERS) {
                if (defineClassMethod == null) {
                    defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
                            new Class[]{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
                    try {
                        defineClassMethod.setAccessible(true);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return defineClassMethod;
    }

    static AccessClassLoader get(Class type) {
        ClassLoader parent = getParentClassLoader(type);
        // 1. fast-path:
        if (SELF_CONTEXT_PARENT_CLASS_LOADER.equals(parent)) {
            if (selfContextAccessClassLoader == null) {
                synchronized (ACCESS_CLASS_LOADERS) {
                    if (selfContextAccessClassLoader == null) {
                        selfContextAccessClassLoader = new AccessClassLoader(SELF_CONTEXT_PARENT_CLASS_LOADER);
                    }
                }
            }
            return selfContextAccessClassLoader;
        }
        // 2. normal search:
        synchronized (ACCESS_CLASS_LOADERS) {
            WeakReference<AccessClassLoader> ref = ACCESS_CLASS_LOADERS.get(parent);
            if (ref != null) {
                AccessClassLoader accessClassLoader = ref.get();
                if (accessClassLoader != null) {
                    return accessClassLoader;
                } else {
                    ACCESS_CLASS_LOADERS.remove(parent);
                }
            }
            AccessClassLoader accessClassLoader = new AccessClassLoader(parent);
            ACCESS_CLASS_LOADERS.put(parent, new WeakReference<AccessClassLoader>(accessClassLoader));
            return accessClassLoader;
        }
    }

    static public void remove(ClassLoader parent) {
        // 1. fast-path:
        if (SELF_CONTEXT_PARENT_CLASS_LOADER.equals(parent)) {
            selfContextAccessClassLoader = null;
        } else {
            // 2. normal search:
            synchronized (ACCESS_CLASS_LOADERS) {
                ACCESS_CLASS_LOADERS.remove(parent);
            }
        }
    }

    static public int activeAccessClassLoaders() {
        int sz = ACCESS_CLASS_LOADERS.size();
        if (selfContextAccessClassLoader != null) {
            sz++;
        }
        return sz;
    }
}
