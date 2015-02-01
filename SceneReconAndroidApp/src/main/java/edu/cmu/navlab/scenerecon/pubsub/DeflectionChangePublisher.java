package edu.cmu.navlab.scenerecon.pubsub;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DeflectionChangePublisher implements SensorEventListener {
    private static final Object SINGLETON_LOCK = new Object();

    private static DeflectionChangePublisher sInstance = null;

    private final Object mCountLock;
    private final Object mChangeLock;

    private Context mApplicationContext;
    private int mResumeCount;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;
    private Set<DeflectionChangeSubscriber> mSubscribers;
    private float[] mGravity;
    private float[] mGeomagnetic;

    public static DeflectionChangePublisher getInstance(final Context context) {
        if (context == null) {
           throw new NullPointerException("context cannot be null");
        }

        if (sInstance == null) {
            synchronized (SINGLETON_LOCK) {
                if (sInstance == null) {
                    sInstance = new DeflectionChangePublisher(context);
                }
            }
        }

        if (!sInstance.getApplicationContext().equals(context.getApplicationContext())) {
            throw new IllegalStateException("Invalid context!");
        }

        return sInstance;
    }

    private DeflectionChangePublisher(final Context context) {
        mApplicationContext = context.getApplicationContext();
        mResumeCount = 0;
        mCountLock = new Object();
        mChangeLock = new Object();
        mSubscribers = Collections.synchronizedSet(new HashSet<DeflectionChangeSubscriber>());

        mSensorManager =
                (SensorManager) mApplicationContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public void onResume() {
        synchronized (mCountLock) {
            if (++mResumeCount == 1) {
                mSensorManager.registerListener(this, mAccelerometerSensor,
                        SensorManager.SENSOR_DELAY_GAME);
                mSensorManager.registerListener(this, mMagneticFieldSensor,
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

    public void addSubscriber(final DeflectionChangeSubscriber subscriber) {
        mSubscribers.add(subscriber);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (mChangeLock) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic = event.values;
            }
            if (mGravity != null && mGeomagnetic != null) {
                float r[] = new float[9];
                float i[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(r, i, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(r, orientation);

                    final double degrees = Math.toDegrees(orientation[0]);
                    for (final DeflectionChangeSubscriber subscriber : mSubscribers) {
                        subscriber.onDeflectionChanged(degrees);
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
