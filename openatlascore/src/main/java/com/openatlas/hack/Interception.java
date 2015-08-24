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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Interception {

    private interface Intercepted {
    }

    public static abstract class InterceptionHandler<T> implements
            InvocationHandler {
        private T mDelegatee;

        @Override
        public Object invoke(Object obj, Method method, Object[] args)
                throws Throwable {
            Object obj2 = null;
            try {
                obj2 = method.invoke(delegatee(), args);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                throw e3.getTargetException();
            }
            return obj2;
        }

        protected T delegatee() {
            return this.mDelegatee;
        }

        void setDelegatee(T t) {
            this.mDelegatee = t;
        }
    }

    public static Object proxy(Object obj, Class cls,
                               InterceptionHandler interceptionHandler)
            throws IllegalArgumentException {
        if (obj instanceof Intercepted) {
            return obj;
        }
        interceptionHandler.setDelegatee(obj);
        return Proxy.newProxyInstance(Interception.class.getClassLoader(),
                new Class[]{cls, Intercepted.class}, interceptionHandler);
    }

    public static Object proxy(Object obj,
                               InterceptionHandler interceptionHandler, Class<?>... clsArr)
            throws IllegalArgumentException {
        interceptionHandler.setDelegatee(obj);
        return Proxy.newProxyInstance(Interception.class.getClassLoader(),
                clsArr, interceptionHandler);
    }

    private Interception() {
    }
}
