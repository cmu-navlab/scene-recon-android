package edu.cmu.navlab.scenerecon.pubsub;

public interface DeflectionChangeSubscriber {
    void onDeflectionChanged(final double degrees);
}
