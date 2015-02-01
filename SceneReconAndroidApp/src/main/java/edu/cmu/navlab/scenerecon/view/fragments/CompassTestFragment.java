package edu.cmu.navlab.scenerecon.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.cmu.navlab.scenerecon.R;

public class CompassTestFragment extends Fragment implements SensorEventListener {
    private final Object mLock;
    private final float mYOffset;
    private final float[] mScale;
    private final float[] mLastValues;
    private final float[] mLastDirections;
    private final float[][] mLastExtremes;
    private final float mLimit = 4;
    private final float mLastDiff[] = new float[3*2];

    private int mLastMatch = -1;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private int mStepCount = 0;

    public CompassTestFragment() {
        super();

        mLastValues = new float[3 * 2];
        mLastDirections = new float[3 * 2];
        mLastExtremes = new float[][]{ new float[3*2], new float[3*2] };
        int h = 480;
        mYOffset = h * 0.5f;
        mScale = new float[2];
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        mLock = new Object();
    }

    @Override
    public void onCreate(final Bundle b) {
        super.onCreate(b);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compass_test, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
        //mSensorManager.registerListener(this, mMagneticFieldSensor,
        //        SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                int j = 1;
                float vSum = 0;
                for (int i = 0; i < 3; i++) {
                    final float v = mYOffset + event.values[i] * mScale[j];
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

                    if (diff > mLimit) {

                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            mStepCount++;
                            mLastMatch = extType;
                            final TextView orientationText = (TextView)
                                    getActivity().findViewById(R.id.test_compass_textView);
                            orientationText.setText(mStepCount + " steps!");
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
        /*synchronized (mLock) {
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

                    final TextView mOrientationText = (TextView)
                            getActivity().findViewById(R.id.test_compass_textView);
                    mOrientationText.setText(Math.round(Math.toDegrees(orientation[0])) + "," +
                            Math.round(Math.toDegrees(orientation[1])) + "," +
                            Math.round(Math.toDegrees(orientation[2])));
                }
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
