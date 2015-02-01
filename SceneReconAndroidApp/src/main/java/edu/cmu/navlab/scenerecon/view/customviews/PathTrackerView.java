package edu.cmu.navlab.scenerecon.view.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.navlab.scenerecon.pubsub.StepVectorSubscriber;

public class PathTrackerView extends View implements StepVectorSubscriber {
    private static final int STEP_LENGTH = 100;
    private static final double MARGIN_FACTOR = 0.1;

    private final Object mLock;
    private final List<Point> mStepPoints;
    private final Paint mRedPaint;
    private final Paint mBluePaint;
    private final Paint mTextPaint;

    public PathTrackerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        mLock = new Object();

        mStepPoints = new ArrayList<>();
        mStepPoints.add(new Point(0, 0));

        mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRedPaint.setColor(Color.RED);

        mBluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBluePaint.setColor(Color.BLUE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(50);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        final List<Point> scaledPoints = scaleToFit();
        for (int i = 1; i < scaledPoints.size(); i++) {
            final Point firstPoint = scaledPoints.get(i - 1);
            final Point secondPoint = scaledPoints.get(i);
            canvas.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y,
                    i % 2 == 0 ? mRedPaint : mBluePaint);
        }
        canvas.drawText(Integer.toString(scaledPoints.size() - 1), 10, 200, mTextPaint);
    }

    private List<Point> scaleToFit() {
        synchronized (mLock) {
            if (mStepPoints.size() == 1) {
                final Point stepPoint = mStepPoints.get(0);
                final List<Point> copiedPoints = new ArrayList<>();
                copiedPoints.add(new Point(stepPoint.x, stepPoint.y));
                return copiedPoints;
            } else {
                int minX = getWidth();
                int minY = getHeight();
                int maxX = -1;
                int maxY = -1;

                for (final Point p : mStepPoints) {
                    if (p.x < minX) {
                        minX = p.x;
                    } else if (p.x > maxX) {
                        maxX = p.x;
                    }

                    if (p.y < minY) {
                        minY = p.y;
                    } else if (p.y > maxY) {
                        maxY = p.y;
                    }
                }

                double maxPolyWidth = ((1 - 2*MARGIN_FACTOR) * getWidth());
                double maxPolyHeight = ((1 - 2*MARGIN_FACTOR) * getHeight());

                double xFactor = maxPolyWidth / (maxX - minX);
                double yFactor = maxPolyHeight / (maxY - minY);
                double factor = Math.min(xFactor, yFactor);

                double xMargin = MARGIN_FACTOR * getWidth();
                double yMargin = MARGIN_FACTOR * getHeight();

                final List<Point> scaledPoints = new ArrayList<>();
                for (final Point stepPoint : mStepPoints) {
                    scaledPoints.add(new Point(
                        (int) Math.round(((stepPoint.x - minX) * factor) + xMargin),
                        (int) Math.round(((stepPoint.y - minY) * factor) + yMargin)
                    ));
                }
                return scaledPoints;
            }
        }
    }

    @Override
    public void onStep(double degrees) {
        final double cosTheta = Math.cos(Math.toRadians(degrees));
        final double sinTheta = Math.sin(Math.toRadians(degrees));

        // Rotate (STEP_LENGTH, 0)
        final Point rotatedPoint = new Point(
            (int) Math.round(STEP_LENGTH * cosTheta),
            (int) Math.round(STEP_LENGTH * sinTheta)
        );

        synchronized (mLock) {
            final Point prevPoint = mStepPoints.get(mStepPoints.size() - 1);
            final Point newPoint = new Point(
                rotatedPoint.x + prevPoint.x,
                rotatedPoint.y + prevPoint.y
            );
            mStepPoints.add(newPoint);
        }

        invalidate();
    }
}
