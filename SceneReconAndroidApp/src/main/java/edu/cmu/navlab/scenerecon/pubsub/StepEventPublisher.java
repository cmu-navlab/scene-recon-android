package edu.cmu.navlab.scenerecon.pubsub;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StepEventPublisher implements SensorEventListener {
    private static final Object SINGLETON_LOCK = new Object();
    private static final float LIMIT = 3.0f;

    private static StepEventPublisher sInstance = null;

    private final Object mCountLock;
    private final Object mChangeLock;
    private final float mScale;
    private final float mYOffset;
    private final float[] mLastValues;
    private final float[] mLastDirections;
    private final float[][] mLastExtremes;
    private final float[] mLastDiff;

    private Context mApplicationContext;
    private int mResumeCount;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Set<StepEventSubscriber> mSubscribers;
    private int mLastMatch;

    public static StepEventPublisher getInstance(final Context context) {
        if (context == null) {
           throw new NullPointerException("context cannot be null");
        }

        if (sInstance == null) {
            synchronized (SINGLETON_LOCK) {
                if (sInstance == null) {
                    sInstance = new StepEventPublisher(context);
                }
            }
        }

        if (!sInstance.getApplicationContext().equals(context.getApplicationContext())) {
            throw new IllegalStateException("Invalid context!");
        }

        return sInstance;
    }

    private StepEventPublisher(final Context context) {
        mApplicationContext = context.getApplicationContext();
        mResumeCount = 0;
        mCountLock = new Object();
        mChangeLock = new Object();
        mSubscribers = Collections.synchronizedSet(new HashSet<StepEventSubscriber>());
        mLastMatch = -1;

        int h = 480;
        mYOffset = h * 0.5f;
        mScale = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        mLastValues = new float[3 * 2];
        mLastDirections = new float[3 * 2];
        mLastExtremes = new float[][]{ new float[3*2], new float[3*2] };
        mLastDiff = new float[3*2];

        mSensorManager =
                (SensorManager) mApplicationContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public void onResume() {
        synchronized (mCountLock) {
            if (++mResumeCount == 1) {
                mSensorManager.registerListener(this, mAccelerometerSensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    public void onPause() {
        synchronized (mCountLock) {
            if (--mResumeCount == 0) {
                mSensorManager.unregisterListener(this);
            }
        }
    }

    public void addSubscriber(final StepEventSubscriber subscriber) {
        mSubscribers.add(subscriber);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (mChangeLock) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float vSum = 0;
                for (int i = 0; i < 3; i++) {
                    final float v = mYOffset + event.values[i] * mScale;
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;

                float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                if (direction == -mLastDirections[k]) {
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                    if (diff > LIMIT) {

                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            mLastMatch = extType;
                            for (final StepEventSubscriber subscriber : mSubscribers) {
                                subscriber.onStep();
                            }
                        } else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
