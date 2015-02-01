package edu.cmu.navlab.scenerecon.view.activities;

import android.app.Fragment;

import edu.cmu.navlab.scenerecon.R;
import edu.cmu.navlab.scenerecon.view.fragments.CompassTestFragment;
import edu.cmu.navlab.scenerecon.view.fragments.SensorTestFragment;

/**
 * CompassTestActivity is the activity that hosts CompassTestFragment
 */
public class CompassTestActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CompassTestFragment();
    }
}
