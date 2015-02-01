package edu.cmu.navlab.scenerecon.view.activities;

import android.app.Fragment;

import edu.cmu.navlab.scenerecon.view.fragments.PathTestFragment;

/**
 * CompassTestActivity is the activity that hosts CompassTestFragment
 */
public class PathTestActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PathTestFragment();
    }
}
