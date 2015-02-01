package edu.cmu.navlab.scenerecon.pubsub;

import android.content.Context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StepVectorPublisher implements DeflectionChangeSubscriber, StepEventSubscriber {
    private static final Object SINGLETON_LOCK = new Object();

    private static StepVectorPublisher sInstance = null;

    private final StepEventPublisher mStepEventPublisher;
    private final DeflectionChangePublisher mDeflectionChangePublisher;

    private Context mApplicationContext;
    private Set<StepVectorSubscriber> mSubscribers;
    private double mDeflection;

    public static StepVectorPublisher getInstance(final Context context) {
        if (context == null) {
           throw new NullPointerException("context cannot be null");
        }

        if (sInstance == null) {
            synchronized (SINGLETON_LOCK) {
                if (sInstance == null) {
                    sInstance = new StepVectorPublisher(context);
                }
            }
        }

        if (!sInstance.getApplicationContext().equals(context.getApplicationContext())) {
            throw new IllegalStateException("Invalid context!");
        }

        return sInstance;
    }

    private StepVectorPublisher(final Context context) {
        mApplicationContext = context.getApplicationContext();
        mSubscribers = Collections.synchronizedSet(new HashSet<StepVectorSubscriber>());
        mDeflection = 0;

        mStepEventPublisher = StepEventPublisher.getInstance(context);
        mStepEventPublisher.addSubscriber(this);
        mDeflectionChangePublisher = DeflectionChangePublisher.getInstance(context);
        mDeflectionChangePublisher.addSubscriber(this);
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public void onResume() {
        mStepEventPublisher.onResume();
        mDeflectionChangePublisher.onResume();
    }

    public void onPause() {
        mStepEventPublisher.onPause();
        mDeflectionChangePublisher.onPause();
    }

    public void addSubscriber(final StepVectorSubscriber subscriber) {
        mSubscribers.add(subscriber);
    }

    @Override
    public void onDeflectionChanged(double degrees) {
        mDeflection = degrees;
    }

    @Override
    public void onStep() {
        for (final StepVectorSubscriber subscriber : mSubscribers) {
            subscriber.onStep(mDeflection);
        }
    }
}
