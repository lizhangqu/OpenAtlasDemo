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
package com.openatlas.android.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Debug;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Coordinator {
    private static final String TAG = "Coord";
    private static final Executor mExecutor;
    static final Queue<TaggedRunnable> mIdleTasks;
    private static final BlockingQueue<Runnable> mPoolWorkQueue;

    public static class CoordinatorRejectHandler implements
            RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable runnable,
                                      ThreadPoolExecutor threadPoolExecutor) {
            Object[] toArray = mPoolWorkQueue.toArray();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('[');
            for (Object obj : toArray) {
                if (obj.getClass().isAnonymousClass()) {
                    stringBuilder.append(getOuterClass(obj));
                    stringBuilder.append(',').append(' ');
                } else {
                    stringBuilder.append(obj.getClass());
                    stringBuilder.append(',').append(' ');
                }
            }
            stringBuilder.append(']');
            throw new RejectedExecutionException("Task " + runnable.toString()
                    + " rejected from " + threadPoolExecutor.toString()
                    + " in " + stringBuilder.toString());
        }

        private Object getOuterClass(Object obj) {
            try {
                Field declaredField = obj.getClass().getDeclaredField("this$0");
                declaredField.setAccessible(true);
                obj = declaredField.get(obj);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (IllegalArgumentException e3) {
                e3.printStackTrace();
            }
            return obj;
        }
    }

    public static abstract class TaggedRunnable implements Runnable {
        public final String tag;

        public TaggedRunnable(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return getClass().getName() + "@" + this.tag;
        }
    }

    static class PostTask extends AsyncTask<Void, Void, Void> {
        private final TaggedRunnable mTaggedRunnable;

        @Override
        protected Void doInBackground(Void... params) {
            return process(params);
        }

        public PostTask(TaggedRunnable taggedRunnable) {
            this.mTaggedRunnable = taggedRunnable;
        }

        protected Void process(Void... params) {
            Coordinator.runWithTiming(this.mTaggedRunnable);
            return null;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + this.mTaggedRunnable;
        }

    }

    @TargetApi(11)
    public static void postTask(TaggedRunnable taggedRunnable) {
        PostTask aVar = new PostTask(taggedRunnable);
        if (VERSION.SDK_INT < 11) {
            aVar.execute();
        } else {
            aVar.executeOnExecutor(mExecutor);
        }
    }

    public static void postTasks(TaggedRunnable... taggedRunnableArr) {
        for (TaggedRunnable taggedRunnable : taggedRunnableArr) {
            if (taggedRunnable != null) {
                postTask(taggedRunnable);
            }
        }
    }

    public static void postIdleTask(TaggedRunnable taggedRunnable) {
        mIdleTasks.add(taggedRunnable);
    }

    public static void runTask(TaggedRunnable taggedRunnable) {
        runWithTiming(taggedRunnable);
    }

    public static void runTasks(TaggedRunnable... taggedRunnableArr) {
        for (TaggedRunnable taggedRunnable : taggedRunnableArr) {
            if (taggedRunnable != null) {
                runWithTiming(taggedRunnable);
            }
        }
    }

    public static void scheduleIdleTasks() {
        Looper.myQueue().addIdleHandler(new IdleHandler() {

            @Override
            public boolean queueIdle() {
                TaggedRunnable taggedRunnable = Coordinator.mIdleTasks
                        .poll();
                if (taggedRunnable == null) {
                    return false;
                }
                Coordinator.postTask(taggedRunnable);
                return !Coordinator.mIdleTasks.isEmpty();
            }
        });
    }

    private static void runWithTiming(TaggedRunnable taggedRunnable) {
        long nanoTime;
        long j = 0;
        boolean isDebug = true;
        if (isDebug) {
            nanoTime = System.nanoTime();
            j = Debug.threadCpuTimeNanos();
        } else {
            nanoTime = 0;
        }
        try {
            taggedRunnable.run();
            if (isDebug) {
                System.out.println("Timing - "
                        + Thread.currentThread().getName() + " "
                        + taggedRunnable.tag + ": "
                        + ((Debug.threadCpuTimeNanos() - j) / 1000000)
                        + "ms (cpu) / "
                        + ((System.nanoTime() - nanoTime) / 1000000)
                        + "ms (real)");

            }
        } catch (RuntimeException e) {
            System.out.println("Exception in " + taggedRunnable.tag);
            if (isDebug) {
                System.out.println("Timing - "
                        + Thread.currentThread().getName() + " "
                        + taggedRunnable.tag + " (failed): "
                        + ((Debug.threadCpuTimeNanos() - j) / 1000000)
                        + "ms (cpu) / "
                        + ((System.nanoTime() - nanoTime) / 1000000)
                        + "ms (real)");
            }
        } catch (Throwable th) {
            th.printStackTrace();
            int i = 1;
            if (isDebug) {
                System.out.println("Timing - "
                        + Thread.currentThread().getName() + " "
                        + taggedRunnable.tag + (i != 0 ? " (failed): " : ": ")
                        + ((Debug.threadCpuTimeNanos() - j) / 1000000)
                        + "ms (cpu) / "
                        + ((System.nanoTime() - nanoTime) / 1000000)
                        + "ms (real)");
            }
        }
    }

    @TargetApi(11)
    static Executor getDefaultAsyncTaskExecutor() {
        if (VERSION.SDK_INT >= 11) {
            return AsyncTask.SERIAL_EXECUTOR;
        }
        try {
            Field declaredField = AsyncTask.class.getDeclaredField("sExecutor");
            declaredField.setAccessible(true);
            return (Executor) declaredField.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    static Executor getCurrentExecutor() {
        return mExecutor;
    }

    static {
        mIdleTasks = new LinkedList<TaggedRunnable>();
        mPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16,
                1, TimeUnit.SECONDS, mPoolWorkQueue, new ThreadFactory() {

            private final AtomicInteger a = new AtomicInteger(1);


            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "CoordTask #" + this.a.getAndIncrement());
            }
        },
                new CoordinatorRejectHandler());
        mExecutor = threadPoolExecutor;
        SaturativeExecutor
                .installAsDefaultAsyncTaskExecutor(threadPoolExecutor);
    }

    @TargetApi(11)
    private static ThreadPoolExecutor getDefaultThreadPoolExecutor() {
        try {
            return (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
        } catch (Throwable th) {
            Log.e(TAG,
                    "Unexpected failure to get default ThreadPoolExecutor of AsyncTask.",
                    th);
            return null;
        }
    }
}
