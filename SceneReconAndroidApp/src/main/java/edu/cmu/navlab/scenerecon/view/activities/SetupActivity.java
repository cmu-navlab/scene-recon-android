package edu.cmu.navlab.scenerecon.view.activities;

import android.app.Fragment;

import edu.cmu.navlab.scenerecon.view.fragments.SetupFragment;

public class SetupActivity extends SingleFragmentActivity {

    @Override
    Fragment createFragment() {
        return new SetupFragment();
    }
}
