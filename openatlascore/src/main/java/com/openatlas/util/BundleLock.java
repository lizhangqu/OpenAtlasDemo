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
package com.openatlas.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BundleLock {
    static Map<String, ReentrantReadWriteLock> bundleIdentifierMap;

    static {
        bundleIdentifierMap = new HashMap<String, ReentrantReadWriteLock>();
    }

    public static void WriteLock(String location) {
        ReentrantReadWriteLock reentrantReadWriteLock;
        synchronized (bundleIdentifierMap) {
            reentrantReadWriteLock = bundleIdentifierMap.get(location);
            if (reentrantReadWriteLock == null) {
                reentrantReadWriteLock = new ReentrantReadWriteLock();
                bundleIdentifierMap.put(location, reentrantReadWriteLock);
            }
        }
        reentrantReadWriteLock.writeLock().lock();
    }

    public static void WriteUnLock(String location) {
        synchronized (bundleIdentifierMap) {
            ReentrantReadWriteLock reentrantReadWriteLock = bundleIdentifierMap.get(location);
            if (reentrantReadWriteLock == null) {
                return;
            }
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    public static void ReadLock(String location) {
        ReentrantReadWriteLock reentrantReadWriteLock;
        synchronized (bundleIdentifierMap) {
            reentrantReadWriteLock = bundleIdentifierMap.get(location);
            if (reentrantReadWriteLock == null) {
                reentrantReadWriteLock = new ReentrantReadWriteLock();
                bundleIdentifierMap.put(location, reentrantReadWriteLock);
            }
        }
        reentrantReadWriteLock.readLock().lock();
    }

    public static void ReadUnLock(String location) {
        synchronized (bundleIdentifierMap) {
            ReentrantReadWriteLock reentrantReadWriteLock = bundleIdentifierMap.get(location);
            if (reentrantReadWriteLock == null) {
                return;
            }
            reentrantReadWriteLock.readLock().unlock();
        }
    }
}