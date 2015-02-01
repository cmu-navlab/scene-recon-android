package edu.cmu.navlab.scenerecon.view.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.cmu.navlab.scenerecon.R;
import edu.cmu.navlab.scenerecon.pubsub.StepVectorPublisher;
import edu.cmu.navlab.scenerecon.view.customviews.PathTrackerView;

public class PathTestFragment extends Fragment {
    private StepVectorPublisher mStepVectorPublisher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_path_test, container, false);

        mStepVectorPublisher = StepVectorPublisher.getInstance(view.getContext());
        final PathTrackerView pathTrackerView =
                (PathTrackerView) view.findViewById(R.id.path_tracker_view);
        mStepVectorPublisher.addSubscriber(pathTrackerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mStepVectorPublisher.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mStepVectorPublisher.onPause();
    }
}
