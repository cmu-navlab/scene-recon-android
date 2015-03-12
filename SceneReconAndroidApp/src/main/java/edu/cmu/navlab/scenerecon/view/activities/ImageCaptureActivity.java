package edu.cmu.navlab.scenerecon.view.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import edu.cmu.navlab.scenerecon.R;

/**
 * {@code ImageCaptureActivity} is the Activity that holds the main view fragment
 */
public class ImageCaptureActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Load the layout
        setContentView(R.layout.image_capture_activity);
    }
}
