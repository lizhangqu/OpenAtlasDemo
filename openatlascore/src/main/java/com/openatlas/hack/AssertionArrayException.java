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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AssertionArrayException extends Exception {
    private static final long serialVersionUID = 1;
    private List<HackAssertionException> mAssertionErr;

    public AssertionArrayException(String str) {
        super(str);
        this.mAssertionErr = new ArrayList<HackAssertionException>();
    }

    public void addException(HackAssertionException hackAssertionException) {
        this.mAssertionErr.add(hackAssertionException);
    }

    public void addException(List<HackAssertionException> list) {
        this.mAssertionErr.addAll(list);
    }

    public List<HackAssertionException> getExceptions() {
        return this.mAssertionErr;
    }

    public static AssertionArrayException mergeException(
            AssertionArrayException assertionArrayException,
            AssertionArrayException assertionArrayException2) {
        if (assertionArrayException == null) {
            return assertionArrayException2;
        }
        if (assertionArrayException2 == null) {
            return assertionArrayException;
        }
        AssertionArrayException assertionArrayException3 = new AssertionArrayException(
                assertionArrayException.getMessage() + ";"
                        + assertionArrayException2.getMessage());
        assertionArrayException3.addException(assertionArrayException
                .getExceptions());
        assertionArrayException3.addException(assertionArrayException2
                .getExceptions());
        return assertionArrayException3;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (HackAssertionException hackAssertionException : this.mAssertionErr) {
            stringBuilder.append(hackAssertionException.toString()).append(";");
            try {
                if (hackAssertionException.getCause() instanceof NoSuchFieldException) {
                    Field[] declaredFields = hackAssertionException
                            .getHackedClass().getDeclaredFields();
                    stringBuilder
                            .append(hackAssertionException.getHackedClass()
                                    .getName())
                            .append(".")
                            .append(hackAssertionException.getHackedFieldName())
                            .append(";");
                    for (Field name : declaredFields) {
                        stringBuilder.append(name.getName()).append("/");
                    }
                } else if (hackAssertionException.getCause() instanceof NoSuchMethodException) {
                    Method[] declaredMethods = hackAssertionException
                            .getHackedClass().getDeclaredMethods();
                    stringBuilder
                            .append(hackAssertionException.getHackedClass()
                                    .getName())
                            .append("->")
                            .append(hackAssertionException
                                    .getHackedMethodName()).append(";");
                    for (int i = 0; i < declaredMethods.length; i++) {
                        if (hackAssertionException.getHackedMethodName()
                                .equals(declaredMethods[i].getName())) {
                            stringBuilder.append(
                                    declaredMethods[i].toGenericString())
                                    .append("/");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            stringBuilder.append("@@@@");
        }
        return stringBuilder.toString();
    }
}
