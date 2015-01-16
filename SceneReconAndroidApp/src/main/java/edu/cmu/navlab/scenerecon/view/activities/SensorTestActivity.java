package edu.cmu.navlab.scenerecon.view.activities;

import android.app.Fragment;

import edu.cmu.navlab.scenerecon.view.fragments.SensorTestFragment;

/**
 * SensorTestActivity is the activity that hosts SensorTestFragment
 */
public class SensorTestActivity extends SingleFragmentActivity {

    @Override
    Fragment createFragment() {
        return new SensorTestFragment();
    }
}
