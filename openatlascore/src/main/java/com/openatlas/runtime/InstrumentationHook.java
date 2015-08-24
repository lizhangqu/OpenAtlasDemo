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
package com.openatlas.runtime;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.openatlas.boot.PlatformConfigure;
import com.openatlas.bundleInfo.BundleInfoList;
import com.openatlas.framework.BundleClassLoader;
import com.openatlas.framework.BundleImpl;
import com.openatlas.framework.Framework;
import com.openatlas.hack.Hack;
import com.openatlas.hack.Hack.HackDeclaration.HackAssertionException;
import com.openatlas.hack.Hack.HackedClass;
import com.openatlas.hack.Hack.HackedMethod;
import com.openatlas.hack.OpenAtlasHacks;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;
import com.openatlas.log.OpenAtlasMonitor;
import com.openatlas.util.StringUtils;

import org.osgi.framework.BundleException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class InstrumentationHook extends Instrumentation {
    static final Logger log;
    private Context context;
    private Instrumentation mBase;
    private HackedClass<Object> mInstrumentationInvoke;
    private HackedMethod mExecStartActivity;
    private HackedMethod mExecStartActivityFragment;

    private interface ExecStartActivityCallback {
        ActivityResult execStartActivity();
    }

    class ExecStartActivityCallbackImpl implements ExecStartActivityCallback {
        final IBinder contextThread;
        final Intent intent;
        final int requestCode;
        final Activity target;
        final IBinder token;
        final Context who;

        ExecStartActivityCallbackImpl(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode) {
            this.who = context;
            this.contextThread = contextThread;
            this.token = token;
            this.target = target;
            this.intent = intent;
            this.requestCode = requestCode;
        }

        @Override
        public ActivityResult execStartActivity() {
            if (mExecStartActivity == null) {
                throw new NullPointerException("could not hook Instrumentation!");
            }

            try {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    return (ActivityResult) mExecStartActivity.invoke(mBase, this.who, this.contextThread, this.token,
                            this.target, this.intent, this.requestCode, null);
                } else {
                    return (ActivityResult) mExecStartActivity.invoke(mBase, this.who, this.contextThread, this.token,
                            this.target, this.intent, this.requestCode);
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
            // return InstrumentationInvoke.execStartActivity(mBase, this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode);
            // return
            // InstrumentationHook.this.mBase.execStartActivity(this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode);
        }
    }

    class ExecStartActivityCallbackImpl_JELLY_BEAN implements ExecStartActivityCallback {
        final IBinder contextThread;
        final Intent intent;
        final Bundle options;
        final int requestCode;
        final Activity target;
        final IBinder token;
        final Context who;

        ExecStartActivityCallbackImpl_JELLY_BEAN(Context context, IBinder contextThread, IBinder token, Activity activity, Intent intent,
                                                 int requestCode, Bundle options) {
            this.who = context;
            this.contextThread = contextThread;
            this.token = token;
            this.target = activity;
            this.intent = intent;
            this.requestCode = requestCode;
            this.options = options;
        }

        @Override
        public ActivityResult execStartActivity() {
            if (mExecStartActivity == null) {
                throw new NullPointerException("could not hook Instrumentation!");
            }
            try {
                return (ActivityResult) mExecStartActivity.invoke(mBase, this.who, this.contextThread, this.token,
                        this.target, this.intent, this.requestCode, this.options);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
            // return InstrumentationInvoke.execStartActivity(mBase, this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode, this.options);
            // return
            // InstrumentationHook.this.mBase.execStartActivity(this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode, this.options);
        }
    }

    class ExecStartFrgmentImpl_ICE_CREAM_SANDWICH implements ExecStartActivityCallback {
        final IBinder contextThread;
        final Intent intent;
        final int requestCode;
        final Fragment target;
        final IBinder token;
        final Context who;

        ExecStartFrgmentImpl_ICE_CREAM_SANDWICH(Context context, IBinder contextThread, IBinder token, Fragment fragment, Intent intent, int requestCode) {
            this.who = context;
            this.contextThread = contextThread;
            this.token = token;
            this.target = fragment;
            this.intent = intent;
            this.requestCode = requestCode;
        }

        @Override
        public ActivityResult execStartActivity() {
            if (mExecStartActivityFragment == null) {
                throw new NullPointerException("could not hook Instrumentation!");
            }
            try {
                return (ActivityResult) mExecStartActivityFragment.invoke(mBase, this.who, this.contextThread, this.token,
                        this.target, this.intent, this.requestCode);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
            // return InstrumentationInvoke.execStartActivity(mBase, this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode);
            // return
            // InstrumentationHook.this.mBase.execStartActivity(this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode, this.options);

            // return
            // InstrumentationHook.this.mBase.execStartActivity(this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode);
        }
    }

    class ExecStartFrgmentImpl_JELLY_BEAN implements ExecStartActivityCallback {
        final IBinder contextThread;
        final Intent intent;
        final Bundle options;
        final int requestCode;
        final Fragment target;
        final IBinder token;
        final Context who;

        ExecStartFrgmentImpl_JELLY_BEAN(Context context, IBinder contextThread, IBinder token, Fragment fragment, Intent intent,
                                        int requestCode, Bundle options) {
            this.who = context;
            this.contextThread = contextThread;
            this.token = token;
            this.target = fragment;
            this.intent = intent;
            this.requestCode = requestCode;
            this.options = options;
        }

        @Override
        public ActivityResult execStartActivity() {
            if (mExecStartActivityFragment == null) {
                throw new NullPointerException("could not hook Instrumentation!");
            }

            try {
                return (ActivityResult) mExecStartActivityFragment.invoke(mBase, this.who, this.contextThread, this.token,
                        this.target, this.intent, this.requestCode, this.options);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
            // return InstrumentationInvoke.execStartActivity(mBase, this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode, this.options);
            // return
            // InstrumentationHook.this.mBase.execStartActivity(this.who,
            // this.contextThread, this.token, this.target,
            // this.intent, this.requestCode, this.options);
        }
    }

    static {
        log = LoggerFactory.getInstance("InstrumentationHook");
    }

    /****
     *
     * public ActivityResult execStartActivity( Context who, IBinder
     * contextThread, IBinder token, Activity target, Intent intent, int
     * requestCode);
     * ***/
    public InstrumentationHook(Instrumentation instrumentation, Context context) {
        this.context = context;
        this.mBase = instrumentation;
        try {
            mInstrumentationInvoke = Hack.into("android.app.Instrumentation");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                mExecStartActivity = mInstrumentationInvoke.method("execStartActivity", Context.class,
                        IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            } else {
                mExecStartActivity = mInstrumentationInvoke.method("execStartActivity", Context.class,
                        IBinder.class, IBinder.class, Activity.class, Intent.class, int.class);
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1||Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
                mExecStartActivityFragment = mInstrumentationInvoke.method("execStartActivity", Context.class,
                        IBinder.class, IBinder.class, Fragment.class, Intent.class, int.class, Bundle.class);
            } else {
                mExecStartActivityFragment = mInstrumentationInvoke.method("execStartActivity", Context.class,
                        IBinder.class, IBinder.class, Fragment.class, Intent.class, int.class);
            }

        } catch (HackAssertionException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void HandleResourceNotFound(Activity activity, Bundle bundle, Exception exception) {
        if (OpenAtlasHacks.ContextThemeWrapper_mResources != null) {
            String str;
            try {
                List<?> assetPathFromResources = getAssetPathFromResources(OpenAtlasHacks.ContextThemeWrapper_mResources
                        .get(activity));
                str = "(1)Paths in ContextThemeWrapper_mResources:" + assetPathFromResources + " paths in runtime:"
                        + DelegateResources.getAssetHistoryPaths();
            } catch (Exception e) {
                str = "(2)paths in runtime:" + DelegateResources.getAssetHistoryPaths() + " getAssetPath fail: " + e;
            }
            throw new RuntimeException(str, exception);
        }
        throw new RuntimeException("(3)ContextThemeWrapper_mResources is null paths in runtime:"
                + DelegateResources.getAssetHistoryPaths(), exception);
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode) {
        return execStartActivityInternal(this.context, intent, new ExecStartActivityCallbackImpl(who, contextThread, token, target,
                intent, requestCode));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle bundle) {
        return execStartActivityInternal(this.context, intent, new ExecStartActivityCallbackImpl_JELLY_BEAN(who, contextThread, token, target,
                intent, requestCode, bundle));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment fragment,
                                            Intent intent, int requestCode) {
        return execStartActivityInternal(this.context, intent, new ExecStartFrgmentImpl_ICE_CREAM_SANDWICH(who, contextThread, token,
                fragment, intent, requestCode));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment fragment,
                                            Intent intent, int requestCode, Bundle bundle) {
        return execStartActivityInternal(this.context, intent, new ExecStartFrgmentImpl_JELLY_BEAN(who, contextThread, token,
                fragment, intent, requestCode, bundle));
    }


    private ActivityResult execStartActivityInternal(Context context, Intent intent, ExecStartActivityCallback execStartActivityCallback) {
        String packageName = null;
        String mComponentName = null;
        ActivityResult activityResult = null;
        if (intent.getComponent() != null) {
            packageName = intent.getComponent().getPackageName();
            mComponentName = intent.getComponent().getClassName();
        } else {
            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 0);
            if (resolveActivity == null || resolveActivity.activityInfo == null) {
            } else {
                packageName = resolveActivity.activityInfo.packageName;
                mComponentName = resolveActivity.activityInfo.name;
            }
        }
        if (mComponentName == null) {
            try {
                return execStartActivityCallback.execStartActivity();
            } catch (Exception e) {
                log.error("Failed to start Activity for " + packageName + " " + mComponentName + e);
                return activityResult;
            }
        }
        try {
            ClassLoadFromBundle.checkInstallBundleIfNeed(mComponentName);
            if (!StringUtils.equals(context.getPackageName(), packageName)) {
                return execStartActivityCallback.execStartActivity();
            }
            if (DelegateComponent.locateComponent(mComponentName) != null) {
                return execStartActivityCallback.execStartActivity();
            }
            try {
                if (Framework.getSystemClassLoader().loadClass(mComponentName) != null) {
                    return execStartActivityCallback.execStartActivity();
                }
                return activityResult;
            } catch (ClassNotFoundException e) {
                OpenAtlasMonitor.getInstance().trace(OpenAtlasMonitor.BUNDLE_INSTALL_FAIL, mComponentName, "",
                        "Failed to load bundle even in system classloader", e);
                log.error("Can't find class " + mComponentName);
                fallBackToClassNotFoundCallback(context, intent, mComponentName);
                return activityResult;
            }
        } catch (Exception e3) {
            log.error("Failed to load bundle for " + mComponentName + e3);
            fallBackToClassNotFoundCallback(context, intent, mComponentName);
            return activityResult;
        }
    }

    private void fallBackToClassNotFoundCallback(Context context, Intent intent, String className) {
        if (Framework.getClassNotFoundCallback() != null) {
            if (intent.getComponent() == null && !TextUtils.isEmpty(className)) {
                intent.setClassName(context, className);
            }
            if (intent.getComponent() != null) {
                Framework.getClassNotFoundCallback().returnIntent(intent);
            }
        }
    }

    /**
     * Perform instantiation of an {@link Activity} object.  This method is intended for use with
     * unit tests, such as android.test.ActivityUnitTestCase.  The activity will be useable
     * locally but will be missing some of the linkages necessary for use within the sytem.
     *
     * @param clazz The Class of the desired Activity
     * @param context The base context for the activity to use
     * @param token The token for this activity to communicate with
     * @param application The application object (if any)
     * @param intent The intent that started this Activity
     * @param activityInfo ActivityInfo from the manifest
     * @param title The title, typically retrieved from the ActivityInfo record
     * @param parent The parent Activity (if any)
     * @param id The embedded Id (if any)
     * @param lastNonConfigurationInstance Arbitrary object that will be
     * available via {@link Activity#getLastNonConfigurationInstance()
     * Activity.getLastNonConfigurationInstance()}.
     * @return Returns the instantiated activity
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application,
                                Intent intent, ActivityInfo activityInfo, CharSequence title, Activity parent, String id,
                                Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
        Activity newActivity = this.mBase.newActivity(clazz, context, token, application, intent, activityInfo,
                title, parent, id, lastNonConfigurationInstance);
        if (RuntimeVariables.androidApplication.getPackageName().equals(activityInfo.packageName)
                && OpenAtlasHacks.ContextThemeWrapper_mResources != null) {
            OpenAtlasHacks.ContextThemeWrapper_mResources.set(newActivity, RuntimeVariables.delegateResources);
        }
        return newActivity;
    }

    /**
     * Perform instantiation of the process's {@link Activity} object.  The
     * default implementation provides the normal system behavior.
     *
     * @param cl The ClassLoader with which to instantiate the object.
     * @param className The name of the class implementing the Activity
     *                  object.
     * @param intent The Intent object that specified the activity class being
     *               instantiated.
     *
     * @return The newly instantiated Activity object.
     */
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        Activity newActivity;
        String defaultBootActivityName = null;
        try {
            newActivity = this.mBase.newActivity(cl, className, intent);
        } catch (ClassNotFoundException e) {
            ClassNotFoundException classNotFoundException = e;
            CharSequence property = Framework.getProperty(PlatformConfigure.BOOT_ACTIVITY,
                    PlatformConfigure.BOOT_ACTIVITY_DEFAULT);
            if (TextUtils.isEmpty(property)) {
                defaultBootActivityName = PlatformConfigure.BOOT_ACTIVITY_DEFAULT;
            } else {
                @SuppressWarnings("unused")
                CharSequence charSequence = property;
            }
            if (TextUtils.isEmpty(defaultBootActivityName)) {
                throw classNotFoundException;
            }
            @SuppressWarnings("deprecation")
            List<RunningTaskInfo> runningTasks = ((ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE))
                    .getRunningTasks(1);
            if (runningTasks != null && runningTasks.size() > 0
                    && runningTasks.get(0).numActivities > 1
                    && Framework.getClassNotFoundCallback() != null) {
                if (intent.getComponent() == null) {
                    intent.setClassName(this.context, className);
                }
                Framework.getClassNotFoundCallback().returnIntent(intent);
            }
            log.warn("Could not find activity class: " + className);
            log.warn("Redirect to welcome activity: " + defaultBootActivityName);
            newActivity = this.mBase.newActivity(cl, defaultBootActivityName, intent);
        }
        if ((cl instanceof DelegateClassLoader) && OpenAtlasHacks.ContextThemeWrapper_mResources != null) {
            OpenAtlasHacks.ContextThemeWrapper_mResources.set(newActivity, RuntimeVariables.delegateResources);
        }
        return newActivity;
    }

    private List<String> getAssetPathFromResources(Resources resources) {
        try {
            return DelegateResources
                    .getOriginAssetsPath((AssetManager) OpenAtlasHacks.Resources_mAssets.get(resources));
        } catch (Exception e) {
            log.debug("DelegateResource" + e.getCause());
            return null;
        }
    }

    /**
     * Perform calling of an activity's {@link Activity#onCreate}
     * method.  The default implementation simply calls through to that method.
     *
     * @param activity The activity being created.
     * @param icicle The previously frozen state (or null) to pass through to
     *               onCreate().
     */
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (RuntimeVariables.androidApplication.getPackageName().equals(activity.getPackageName())) {
            ContextImplHook contextImplHook = new ContextImplHook(activity.getBaseContext(), activity.getClass()
                    .getClassLoader());
            if (!(OpenAtlasHacks.ContextThemeWrapper_mBase == null || OpenAtlasHacks.ContextThemeWrapper_mBase.getField() == null)) {
                OpenAtlasHacks.ContextThemeWrapper_mBase.set(activity, contextImplHook);
            }
            OpenAtlasHacks.ContextWrapper_mBase.set(activity, contextImplHook);
            if (activity.getClass().getClassLoader() instanceof BundleClassLoader) {
                try {
                    ((BundleClassLoader) activity.getClass().getClassLoader()).getBundle().startBundle();
                } catch (BundleException e) {
                    log.error(e.getMessage() + " Caused by: ", e.getNestedException());
                }
            }
            String property = Framework.getProperty(PlatformConfigure.BOOT_ACTIVITY, PlatformConfigure.BOOT_ACTIVITY);
            if (TextUtils.isEmpty(property)) {
                property = PlatformConfigure.BOOT_ACTIVITY;
            }
            try {
                ensureResourcesInjected(activity);
                this.mBase.callActivityOnCreate(activity, icicle);
                return;
            } catch (Exception e2) {
                if (!e2.toString().contains("android.content.res.Resources")
                        || e2.toString().contains("OutOfMemoryError")) {
                    e2.printStackTrace();
                }
                HandleResourceNotFound(activity, icicle, e2);
                return;
            }
        }
        this.mBase.callActivityOnCreate(activity, icicle);
    }

    @Override
    @TargetApi(18)
    public UiAutomation getUiAutomation() {
        return this.mBase.getUiAutomation();
    }

    @Override
    public void onCreate(Bundle bundle) {
        this.mBase.onCreate(bundle);
    }

    @Override
    public void start() {
        this.mBase.start();
    }

    @Override
    public void onStart() {
        this.mBase.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable th) {
        return this.mBase.onException(obj, th);
    }

    @Override
    public void sendStatus(int i, Bundle bundle) {
        this.mBase.sendStatus(i, bundle);
    }

    @Override
    public void finish(int i, Bundle bundle) {
        this.mBase.finish(i, bundle);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        this.mBase.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        this.mBase.startPerformanceSnapshot();
    }

    @Override
    public void endPerformanceSnapshot() {
        this.mBase.endPerformanceSnapshot();
    }

    private void ensureResourcesInjected(Activity activity) {
        ContextImplHook contextImplHook = new ContextImplHook(activity.getBaseContext(), activity.getClass()
                .getClassLoader());
        if (OpenAtlasHacks.ContextThemeWrapper_mResources != null) {
            try {
                validateActivityResource(activity);
            } catch (Throwable th) {
            }
            OpenAtlasHacks.ContextThemeWrapper_mResources.set(activity, RuntimeVariables.delegateResources);
        }
        if (!(OpenAtlasHacks.ContextThemeWrapper_mBase == null || OpenAtlasHacks.ContextThemeWrapper_mBase.getField() == null)) {
            OpenAtlasHacks.ContextThemeWrapper_mBase.set(activity, contextImplHook);
        }
        OpenAtlasHacks.ContextWrapper_mBase.set(activity, contextImplHook);
    }

    private boolean validateActivityResource(Activity activity) {
        String absolutePath;
        Resources resources;

        String logInfo = null;
        BundleImpl bundleImpl = (BundleImpl) Framework.getBundle(BundleInfoList.getInstance().getBundleNameForComponet(
                activity.getLocalClassName()));
        if (bundleImpl != null) {
            absolutePath = bundleImpl.getArchive().getArchiveFile().getAbsolutePath();
        } else {
            absolutePath = null;
        }
        if (OpenAtlasHacks.ContextThemeWrapper_mResources != null) {
            resources = OpenAtlasHacks.ContextThemeWrapper_mResources.get(activity);
        } else {
            resources = activity.getResources();
        }
        Resources delegateResource = RuntimeVariables.delegateResources;
        if (resources == delegateResource) {
            return true;
        }
        List<?> assetPathFromResources = getAssetPathFromResources(resources);
        String assetHistoryPaths = DelegateResources.getAssetHistoryPaths();
        List<?> assetPathFromDelegateResources = getAssetPathFromResources(delegateResource);
        if (!(absolutePath == null || assetPathFromResources == null || assetPathFromResources.contains(absolutePath))) {
            logInfo = "Activity Resources path not contains:"
                    + bundleImpl.getArchive().getArchiveFile().getAbsolutePath();
            if (!assetHistoryPaths.contains(absolutePath)) {
                logInfo = logInfo + "paths in history not contains:"
                        + bundleImpl.getArchive().getArchiveFile().getAbsolutePath();
            }
            if (!assetPathFromDelegateResources.contains(absolutePath)) {
                logInfo = logInfo + "paths in runtime not contains:"
                        + bundleImpl.getArchive().getArchiveFile().getAbsolutePath();
            }
            if (!bundleImpl.getArchive().getArchiveFile().exists()) {
                logInfo = logInfo + "  Bundle archive file not exist:"
                        + bundleImpl.getArchive().getArchiveFile().getAbsolutePath();
            }
            logInfo = logInfo + " Activity Resources paths length:" + assetPathFromResources.size();
        }
        if (logInfo == null) {
            return true;
        }
        OpenAtlasMonitor.getInstance().trace(Integer.valueOf(-4), "", "", logInfo);
        return false;
    }

    @Override
    public void onDestroy() {
        this.mBase.onDestroy();
    }

    @Override
    public Context getContext() {
        return this.mBase.getContext();
    }

    @Override
    public ComponentName getComponentName() {
        return this.mBase.getComponentName();
    }

    @Override
    public Context getTargetContext() {
        return this.mBase.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
        return this.mBase.isProfiling();
    }

    @Override
    public void startProfiling() {
        this.mBase.startProfiling();
    }

    @Override
    public void stopProfiling() {
        this.mBase.stopProfiling();
    }

    /**
     * Force the global system in or out of touch mode.  This can be used if
     * your instrumentation relies on the UI being in one more or the other
     * when it starts.
     *
     * @param inTouch Set to true to be in touch mode, false to be in
     * focus mode.
     */
    @Override
    public void setInTouchMode(boolean inTouch) {
        this.mBase.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable runnable) {
        this.mBase.waitForIdle(runnable);
    }

    @Override
    public void waitForIdleSync() {
        this.mBase.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runnable) {
        this.mBase.runOnMainSync(runnable);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return this.mBase.startActivitySync(intent);
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        this.mBase.addMonitor(monitor);
    }

    /**
     * A convenience wrapper for {@link #addMonitor(ActivityMonitor)} that
     * creates an intent filter matching {@link ActivityMonitor} for you and
     * returns it.
     *
     * @param filter The set of intents this monitor is responsible for.
     * @param result A canned result to return if the monitor is hit; can
     *               be null.
     * @param block Controls whether the monitor should block the activity
     *              start (returning its canned result) or let the call
     *              proceed.
     *
     * @return The newly created and added activity monitor.
     *
     * @see #addMonitor(ActivityMonitor)
     * @see #checkMonitorHit
     */
    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return this.mBase.addMonitor(filter, result, block);
    }

    /**
     * A convenience wrapper for {@link #addMonitor(ActivityMonitor)} that
     * creates a class matching {@link ActivityMonitor} for you and returns it.
     *
     * @param cls The activity class this monitor is responsible for.
     * @param result A canned result to return if the monitor is hit; can
     *               be null.
     * @param block Controls whether the monitor should block the activity
     *              start (returning its canned result) or let the call
     *              proceed.
     *
     * @return The newly created and added activity monitor.
     *
     * @see #addMonitor(ActivityMonitor)
     * @see #checkMonitorHit
     */
    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return this.mBase.addMonitor(cls, result, block);
    }

    /**
     * Test whether an existing {@link ActivityMonitor} has been hit.  If the
     * monitor has been hit at least <var>minHits</var> times, then it will be
     * removed from the activity monitor list and true returned.  Otherwise it
     * is left as-is and false is returned.
     *
     * @param monitor The ActivityMonitor to check.
     * @param minHits The minimum number of hits required.
     *
     * @return True if the hit count has been reached, else false.
     *
     * @see #addMonitor
     */
    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return this.mBase.checkMonitorHit(monitor, minHits);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return this.mBase.waitForMonitor(monitor);
    }

    /**
     * Wait for an existing {@link ActivityMonitor} to be hit till the timeout
     * expires.  Once the monitor has been hit, it is removed from the activity
     * monitor list and the first created Activity object that matched it is
     * returned.  If the timeout expires, a null object is returned.
     *
     * @param monitor The ActivityMonitor to wait for.
     * @param timeOut The timeout value in secs.
     *
     * @return The Activity object that matched the monitor.
     */
    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return this.mBase.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        this.mBase.removeMonitor(monitor);
    }

    /**
     * Execute a particular menu item.
     *
     * @param targetActivity The activity in question.
     * @param id The identifier associated with the menu item.
     * @param flag Additional flags, if any.
     * @return Whether the invocation was successful (for example, it could be
     *         false if item is disabled).
     */
    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return this.mBase.invokeMenuActionSync(targetActivity, id, flag);
    }

    /**
     * Show the context menu for the currently focused view and executes a
     * particular context menu item.
     *
     * @param targetActivity The activity in question.
     * @param id The identifier associated with the context menu item.
     * @param flag Additional flags, if any.
     * @return Whether the invocation was successful (for example, it could be
     *         false if item is disabled).
     */
    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return this.mBase.invokeContextMenuAction(targetActivity, id, flag);
    }

    /**
     * Sends the key events corresponding to the text to the app being
     * instrumented.
     *
     * @param text The text to be sent.
     */
    @Override
    public void sendStringSync(String text) {
        this.mBase.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent keyEvent) {
        this.mBase.sendKeySync(keyEvent);
    }

    /**
     * Sends an up and down key event sync to the currently focused window.
     *
     * @param key The integer keycode for the event.
     */
    @Override
    public void sendKeyDownUpSync(int key) {
        this.mBase.sendKeyDownUpSync(key);
    }

    /**
     * Higher-level method for sending both the down and up key events for a
     * particular character key code.  Equivalent to creating both KeyEvent
     * objects by hand and calling {@link #sendKeySync}.  The event appears
     * as if it came from keyboard 0, the built in one.
     *
     * @param keyCode The key code of the character to send.
     */
    @Override
    public void sendCharacterSync(int keyCode) {
        this.mBase.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent motionEvent) {
        this.mBase.sendPointerSync(motionEvent);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent motionEvent) {
        this.mBase.sendTrackballEventSync(motionEvent);
    }

    /**
     * Perform instantiation of the process's {@link Application} object.  The
     * default implementation provides the normal system behavior.
     *
     * @param classLoader The ClassLoader with which to instantiate the object.
     * @param className The name of the class implementing the Application
     *                  object.
     * @param context The context to initialize the application with
     *
     * @return The newly instantiated Application object.
     */
    @Override
    public Application newApplication(ClassLoader classLoader, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return this.mBase.newApplication(classLoader, className, context);
    }

    @Override
    public void callApplicationOnCreate(Application application) {
        this.mBase.callApplicationOnCreate(application);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        this.mBase.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle bundle) {
        this.mBase.callActivityOnRestoreInstanceState(activity, bundle);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle bundle) {
        this.mBase.callActivityOnPostCreate(activity, bundle);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        this.mBase.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        this.mBase.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        this.mBase.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        this.mBase.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        this.mBase.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle bundle) {
        this.mBase.callActivityOnSaveInstanceState(activity, bundle);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        this.mBase.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        this.mBase.callActivityOnUserLeaving(activity);
    }

    @Override
    public void startAllocCounting() {
        this.mBase.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
        this.mBase.stopAllocCounting();
    }

    @Override
    public Bundle getAllocCounts() {
        return this.mBase.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
        return this.mBase.getBinderCounts();
    }
}
