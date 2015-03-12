package edu.cmu.navlab.scenerecon.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

import edu.cmu.navlab.scenerecon.R;

// TODO : 1) Move all the "work" to activities and fragments from views
// TODO : 2) Clean up the code (fix variable names etc.) and add comments

public class HomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }

    public void launchPreferences(View view) {
        Intent i = new Intent(this, SceneReconPreferenceActivity.class);
        i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                SceneReconPreferenceActivity.SceneReconPreferenceFragment.class.getName());
        i.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true );
        startActivity(i);
    }
}
