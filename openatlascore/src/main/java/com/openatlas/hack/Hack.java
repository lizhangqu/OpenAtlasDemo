/**
 * OpenAtlasForAndroid Project
 * The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author BunnyBlue
 **/
package com.openatlas.hack;

import com.openatlas.hack.Hack.HackDeclaration.HackAssertionException;
import com.openatlas.hack.Interception.InterceptionHandler;
import com.openatlas.runtime.DelegateClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Hack {
    private static AssertionFailureHandler sFailureHandler;

    public interface AssertionFailureHandler {
        boolean onAssertionFailure(HackAssertionException hackAssertionException);
    }

    public static abstract class HackDeclaration {

        public static class HackAssertionException extends Throwable {
            private static final long serialVersionUID = 1;
            private Class<?> mHackedClass;
            private String mHackedFieldName;
            private String mHackedMethodName;

            public HackAssertionException(String str) {
                super(str);
            }

            public HackAssertionException(Exception exception) {
                super(exception);
            }

            @Override
            public String toString() {
                return getCause() != null ? getClass().getName() + ": "
                        + getCause() : super.toString();
            }

            public Class<?> getHackedClass() {
                return this.mHackedClass;
            }

            public void setHackedClass(Class<?> cls) {
                this.mHackedClass = cls;
            }

            public String getHackedMethodName() {
                return this.mHackedMethodName;
            }

            public void setHackedMethodName(String str) {
                this.mHackedMethodName = str;
            }

            public String getHackedFieldName() {
                return this.mHackedFieldName;
            }

            public void setHackedFieldName(String str) {
                this.mHackedFieldName = str;
            }
        }
    }

    public static class HackedClass<C> {
        protected Class<C> mClass;

        public HackedField<C, Object> staticField(String str)
                throws HackAssertionException {
            return new HackedField(this.mClass, str, 8);
        }

        public HackedField<C, Object> field(String str)
                throws HackAssertionException {
            return new HackedField(this.mClass, str, 0);
        }

        public HackedMethod staticMethod(String str, Class<?>... clsArr)
                throws HackAssertionException {
            return new HackedMethod(this.mClass, str, clsArr, 8);
        }

        public HackedMethod method(String str, Class<?>... clsArr)
                throws HackAssertionException {
            return new HackedMethod(this.mClass, str, clsArr, 0);
        }

        public HackedConstructor constructor(Class<?>... clsArr)
                throws HackAssertionException {
            return new HackedConstructor(this.mClass, clsArr);
        }

        public HackedClass(Class<C> cls) {
            this.mClass = cls;
        }

        public Class<C> getmClass() {
            return this.mClass;
        }
    }

    public static class HackedConstructor {
        protected Constructor<?> mConstructor;

        HackedConstructor(Class<?> cls, Class<?>[] clsArr)
                throws HackAssertionException {
            if (cls != null) {
                try {
                    this.mConstructor = cls.getDeclaredConstructor(clsArr);
                } catch (Exception e) {
                    HackAssertionException hackAssertionException = new HackAssertionException(
                            e);
                    hackAssertionException.setHackedClass(cls);
                    Hack.fail(hackAssertionException);
                }
            }
        }

        public Object getInstance(Object... objArr)
                throws IllegalArgumentException {
            Object obj = null;
            this.mConstructor.setAccessible(true);
            try {
                obj = this.mConstructor.newInstance(objArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }
    }

    public static class HackedField<C, T> {
        private final Field mField;

        public <T2> com.openatlas.hack.Hack.HackedField<C, T2> ofGenericType(
                Class<?> cls) throws HackAssertionException {
            if (!(this.mField == null || cls.isAssignableFrom(this.mField
                    .getType()))) {
                Hack.fail(new HackAssertionException(new ClassCastException(
                        this.mField + " is not of type " + cls)));
            }
            return (HackedField<C, T2>) this;
        }

        public <T2> com.openatlas.hack.Hack.HackedField<C, T2> ofType(
                Class<T2> cls) throws HackAssertionException {
            if (!(this.mField == null || cls.isAssignableFrom(this.mField
                    .getType()))) {
                Hack.fail(new HackAssertionException(new ClassCastException(
                        this.mField + " is not of type " + cls)));
            }
            return (HackedField<C, T2>) this;
        }

        public com.openatlas.hack.Hack.HackedField<C, T> ofType(
                String str) throws HackAssertionException {
            com.openatlas.hack.Hack.HackedField<C, T> ofType = null;
            try {
                ofType = (HackedField<C, T>) ofType(Class.forName(str));
            } catch (Exception e) {
                Hack.fail(new HackAssertionException(e));
            }
            return ofType;
        }

        public T get(C c) {
            try {
                return (T) this.mField.get(c);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void set(C c, Object obj) {
            try {
                this.mField.set(c, obj);
            } catch (Throwable e) {
                e.printStackTrace();
                if (obj instanceof DelegateClassLoader) {
                    throw new RuntimeException("set DelegateClassLoader fail",
                            e);
                }
            }
        }

        public void hijack(C c, InterceptionHandler<?> interceptionHandler) {
            Object obj = get(c);
            if (obj == null) {
                throw new IllegalStateException("Cannot hijack null");
            }
            set(c, Interception.proxy(obj,
                    interceptionHandler, obj.getClass()
                            .getInterfaces()));
        }

        HackedField(Class<C> cls, String str, int i)
                throws HackAssertionException {
            Field field = null;
            if (cls == null) {
                this.mField = null;
                return;
            }
            try {
                field = cls.getDeclaredField(str);
                if (i > 0 && (field.getModifiers() & i) != i) {
                    Hack.fail(new HackAssertionException(field
                            + " does not match modifiers: " + i));
                }
                field.setAccessible(true);
            } catch (Exception e) {
                HackAssertionException hackAssertionException = new HackAssertionException(
                        e);
                hackAssertionException.setHackedClass(cls);
                hackAssertionException.setHackedFieldName(str);
                Hack.fail(hackAssertionException);
            } finally {
                this.mField = field;
            }
        }

        public Field getField() {
            return this.mField;
        }
    }

    public static class HackedMethod {
        protected final Method mMethod;

        public Object invoke(Object obj, Object... objArr)
                throws IllegalArgumentException, InvocationTargetException {
            Object obj2 = null;
            try {
                obj2 = this.mMethod.invoke(obj, objArr);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return obj2;
        }

        HackedMethod(Class<?> cls, String str, Class<?>[] clsArr, int i)
                throws HackAssertionException {
            Method method = null;
            if (cls == null) {
                this.mMethod = null;
                return;
            }
            try {
                method = cls.getDeclaredMethod(str, clsArr);
                if (i > 0 && (method.getModifiers() & i) != i) {
                    Hack.fail(new HackAssertionException(method
                            + " does not match modifiers: " + i));
                }
                method.setAccessible(true);
            } catch (Exception e) {
                HackAssertionException hackAssertionException = new HackAssertionException(
                        e);
                hackAssertionException.setHackedClass(cls);
                hackAssertionException.setHackedMethodName(str);
                Hack.fail(hackAssertionException);
            } finally {
                this.mMethod = method;
            }
        }

        public Method getMethod() {
            return this.mMethod;
        }
    }

    public static <T> HackedClass<T> into(Class<T> cls) {
        return new HackedClass(cls);
    }

    public static <T> HackedClass<T> into(String str)
            throws HackAssertionException {
        try {
            return new HackedClass(Class.forName(str));
        } catch (Exception e) {
            fail(new HackAssertionException(e));
            return new HackedClass(null);
        }
    }

    private static void fail(HackAssertionException hackAssertionException)
            throws HackAssertionException {
        if (sFailureHandler == null
                || !sFailureHandler.onAssertionFailure(hackAssertionException)) {
            throw hackAssertionException;
        }
    }

    public static void setAssertionFailureHandler(
            AssertionFailureHandler assertionFailureHandler) {
        sFailureHandler = assertionFailureHandler;
    }

    private Hack() {
    }
}
