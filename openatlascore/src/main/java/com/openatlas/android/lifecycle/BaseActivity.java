/**
 * OpenAtlasForAndroid Project
 * The MIT License (MIT) Copyright (AwbDebug) 2015 Bunny Blue,achellies
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
package com.openatlas.android.lifecycle;

import android.app.Activity;
import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseActivity extends Activity {
    private final List<IndividualActivityLifecycleCallback> mIndividualActivityLifecycleCallbacks;

    public interface IndividualActivityLifecycleCallback {
        void onCreated(Activity activity);

        void onDestroyed(Activity activity);

        void onPaused(Activity activity);

        void onResumed(Activity activity);

        void onStarted(Activity activity);

        void onStopped(Activity activity);
    }

    public BaseActivity() {
        this.mIndividualActivityLifecycleCallbacks = new CopyOnWriteArrayList<IndividualActivityLifecycleCallback>();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!this.mIndividualActivityLifecycleCallbacks.isEmpty()) {
            for (IndividualActivityLifecycleCallback individualActivityLifecycleCallback : this.mIndividualActivityLifecycleCallbacks) {
                individualActivityLifecycleCallback.onCreated(this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!this.mIndividualActivityLifecycleCallbacks.isEmpty()) {
            for (IndividualActivityLifecycleCallback individualActivityLifecycleCallback : this.mIndividualActivityLifecycleCallbacks) {
                individualActivityLifecycleCallback.onStarted(this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.mIndividualActivityLifecycleCallbacks.isEmpty()) {
            for (IndividualActivityLifecycleCallback individualActivityLifecycleCallback : this.mIndividualActivityLifecycleCallbacks) {
                individualActivityLifecycleCallback.onResumed(this);
            }
        }
    }

    @Override
    protected void onPause() {
        if (!this.mIndividualActivityLifecycleCallbacks.isEmpty()) {
            for (IndividualActivityLifecycleCallback individualActivityLifecycleCallback : this.mIndividualActivityLifecycleCallbacks) {
                individualActivityLifecycleCallback.onPaused(this);
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (!this.mIndividualActivityLifecycleCallbacks.isEmpty()) {
            for (IndividualActivityLifecycleCallback individualActivityLifecycleCallback : this.mIndividualActivityLifecycleCallbacks) {
                individualActivityLifecycleCallback.onStopped(this);
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (!this.mIndividualActivityLifecycleCallbacks.isEmpty()) {
            for (IndividualActivityLifecycleCallback individualActivityLifecycleCallback : this.mIndividualActivityLifecycleCallbacks) {
                individualActivityLifecycleCallback.onDestroyed(this);
            }
        }
        super.onDestroy();
    }

    public void registerIndividualActivityLifecycleCallback(
            IndividualActivityLifecycleCallback individualActivityLifecycleCallback) {
        this.mIndividualActivityLifecycleCallbacks
                .add(individualActivityLifecycleCallback);
    }

    public void unregisterIndividualActivityLifecycleCallback(
            IndividualActivityLifecycleCallback individualActivityLifecycleCallback) {
        this.mIndividualActivityLifecycleCallbacks
                .remove(individualActivityLifecycleCallback);
    }

}
