package edu.cmu.navlab.scenerecon.view.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;
import android.widget.Toast;

import java.util.List;

import edu.cmu.navlab.scenerecon.R;

public class SceneReconPreferenceActivity extends PreferenceActivity {

    public SceneReconPreferenceActivity() {
        super();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    public static class SceneReconPreferenceFragment extends PreferenceFragment {
        private Context mContext;

        @Override
        public void onAttach(final Activity actvity) {
            super.onAttach(actvity);
            mContext = actvity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference);

            // Setup validation listeners
            findPreference("no_circles").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (((String) newValue).length() == 0) {
                                Toast.makeText(mContext, "Value cannot be blank",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }

                            Integer intVal = Integer.parseInt((String) newValue);
                            if (intVal <= 1) {
                                Toast.makeText(mContext, "Value must be greater than 1",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            } else if(intVal % 2 == 0) {
                                Toast.makeText(mContext, "Value must be odd",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            } else {
                                int sqrt = (int) Math.sqrt(intVal);
                                if (sqrt * sqrt != intVal) {
                                    Toast.makeText(mContext, "Value must be a perfect square",
                                            Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
            );

            findPreference("circles_dist").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (((String) newValue).length() == 0) {
                                Toast.makeText(mContext, "Value cannot be blank",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }

                            Integer intVal = Integer.parseInt((String) newValue);
                            if (intVal < 10 || intVal > 100) {
                                Toast.makeText(mContext,
                                        "Value must be between (inclusive) 10 and 100",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }
                            return true;
                        }
                    }
            );

            findPreference("smooth_coeff").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (((String) newValue).length() == 0) {
                                Toast.makeText(mContext, "Value cannot be blank",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }

                            Float floatVal = Float.parseFloat((String) newValue);
                            if (floatVal < 0 || floatVal >= 1) {
                                Toast.makeText(mContext,
                                        "Value must be greater than or equal to 0 but less than 1",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }
                            return true;
                        }
                    }
            );

            findPreference("canvas_width_degrees").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (((String) newValue).length() == 0) {
                                Toast.makeText(mContext, "Value cannot be blank",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }

                            Float floatVal = Float.parseFloat((String) newValue);
                            if (floatVal <= 0 || floatVal >= 360) {
                                Toast.makeText(mContext,
                                        "Value must be greater than 0 and less than 360",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }
    }

}
