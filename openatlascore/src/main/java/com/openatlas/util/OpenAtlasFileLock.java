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

import android.os.Process;

import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;
import com.openatlas.runtime.RuntimeVariables;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class OpenAtlasFileLock {
    static final Logger log = LoggerFactory.getInstance("AtlasFileLock");
    private static String processName;
    private static OpenAtlasFileLock singleton;
    private Map<String, FileLockCount> mRefCountMap = new ConcurrentHashMap();

    private class FileLockCount {
        FileChannel fChannel;
        RandomAccessFile fOs;
        FileLock mFileLock;
        int mRefCount;

        FileLockCount(FileLock fileLock, int i, RandomAccessFile randomAccessFile, FileChannel fileChannel) {
            this.mFileLock = fileLock;
            this.mRefCount = i;
            this.fOs = randomAccessFile;
            this.fChannel = fileChannel;
        }
    }

    static {
        int myPid = Process.myPid();
        if (RuntimeVariables.androidApplication.getApplicationContext() != null) {
            processName = OpenAtlasUtils.getProcessNameByPID(Process.myPid());
        }
    }

    public static OpenAtlasFileLock getInstance() {

        if (singleton == null) {
            singleton = new OpenAtlasFileLock();
        }
        return singleton;
    }

    private int RefCntInc(String filePath, FileLock fileLock, RandomAccessFile randomAccessFile, FileChannel fileChannel) {
        Integer valueOf;

        Integer.valueOf(0);
        if (this.mRefCountMap.containsKey(filePath)) {
            FileLockCount fileLockCount = this.mRefCountMap.get(filePath);
            int i = fileLockCount.mRefCount;
            fileLockCount.mRefCount = i + 1;
            valueOf = Integer.valueOf(i);
        } else {
            Integer valueOf2 = Integer.valueOf(1);
            this.mRefCountMap.put(filePath, new FileLockCount(fileLock, valueOf2.intValue(), randomAccessFile, fileChannel));
            valueOf = valueOf2;
        }
        return valueOf.intValue();
    }

    private int RefCntDec(String filePath) {

        Integer valueOf = Integer.valueOf(0);
        if (this.mRefCountMap.containsKey(filePath)) {
            FileLockCount fileLockCount = this.mRefCountMap.get(filePath);
            int i = fileLockCount.mRefCount - 1;
            fileLockCount.mRefCount = i;
            valueOf = Integer.valueOf(i);
            if (valueOf.intValue() <= 0) {
                this.mRefCountMap.remove(filePath);
            }
        }
        return valueOf.intValue();
    }

    public boolean LockExclusive(File file) {

        if (file == null) {
            return false;
        }
        try {
            File file2 = new File(file.getParentFile().getAbsolutePath().concat("/lock"));
            if (!file2.exists()) {
                file2.createNewFile();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file2.getAbsolutePath(), "rw");
            FileChannel channel = randomAccessFile.getChannel();
            FileLock lock = channel.lock();
            if (!lock.isValid()) {
                return false;
            }
            RefCntInc(file2.getAbsolutePath(), lock, randomAccessFile, channel);
            return true;
        } catch (Exception e) {
            log.error(processName + " FileLock " + file.getParentFile().getAbsolutePath().concat("/lock") + " Lock FAIL! " + e.getMessage());
            return false;
        }
    }

    public void unLock(File file) {

        File file2 = new File(file.getParentFile().getAbsolutePath().concat("/lock"));
        if (!file2.exists()) {
            return;
        }
        if (file2 == null || this.mRefCountMap.containsKey(file2.getAbsolutePath())) {
            FileLockCount fileLockCount = this.mRefCountMap.get(file2.getAbsolutePath());
            if (fileLockCount != null) {
                FileLock fileLock = fileLockCount.mFileLock;
                RandomAccessFile randomAccessFile = fileLockCount.fOs;
                FileChannel fileChannel = fileLockCount.fChannel;
                try {
                    if (RefCntDec(file2.getAbsolutePath()) <= 0) {
                        if (fileLock != null && fileLock.isValid()) {
                            fileLock.release();
                        }
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                        }
                        if (fileChannel != null) {
                            fileChannel.close();
                        }
                    }
                } catch (IOException e) {
                    log.error(processName + " FileLock " + file.getParentFile().getAbsolutePath().concat("/lock") + " unlock FAIL! " + e.getMessage());
                }
            }
        }
    }
}