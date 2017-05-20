package com.wolfteck.smoothtouch2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BoltCircleDiagram extends View {

    private int strokeWidth = 1;
    private Paint bcPaintSolid;
    private Paint bcPaintDotted;
    private RectF rectF;
    private int numberOfHoles;
    private float circleDiameter;
    private float firstAngle;
    private float arcLength;
    private float holeDiameter;

    public BoltCircleDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        bcPaintSolid = new Paint();
        bcPaintSolid.setStyle(Paint.Style.STROKE);
        bcPaintSolid.setStrokeWidth(strokeWidth);

        bcPaintDotted = new Paint();
        bcPaintDotted.setStyle(Paint.Style.STROKE);
        bcPaintDotted.setStrokeWidth(strokeWidth);
        bcPaintDotted.setPathEffect(new DashPathEffect(new float[] {10,20},0));
    }

    public void setNumberOfHoles(int numberOfHoles) {
        this.numberOfHoles = numberOfHoles;
        invalidate();
    }

    public void setCircleDiameter(float circleDiameter) {
        this.circleDiameter = circleDiameter;
        invalidate();
    }

    public void setFirstAngle(float firstAngle) {
        this.firstAngle = firstAngle;
        invalidate();
    }

    public void setArcLength(float arcLength) {
        this.arcLength = arcLength;
        invalidate();
    }

    public void setHoleDiameter(float holeDiameter) {
        this.holeDiameter = holeDiameter;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(numberOfHoles > 0) {
            float drillArc;
            int height = getHeight();
            int width = getWidth();
            if(height > width) {
                height = width;
            } else {
                width = height;
            }

            float realHeight = circleDiameter + holeDiameter;
            float scale = height / realHeight;

            float holeRadius = scale * holeDiameter / 2;
            float circleRadius = scale * circleDiameter / 2 - strokeWidth;

            rectF = new RectF(holeRadius + strokeWidth, holeRadius + strokeWidth, width - holeRadius - strokeWidth, height - holeRadius - strokeWidth);

            canvas.drawArc(rectF, 0, -firstAngle, true, bcPaintSolid);
            canvas.drawArc(rectF, -firstAngle, -arcLength, true, bcPaintDotted);

            if(arcLength == 360) { drillArc = 360 - 360 / numberOfHoles; } else { drillArc = arcLength; }
            float segment = drillArc / (numberOfHoles-1);
            float segStartPoint = -firstAngle;

            for(int i = 0; i < numberOfHoles; i++) {
                canvas.drawCircle(circleRadius * (float) Math.cos(Math.toRadians(segStartPoint)) + height/2, circleRadius * (float) Math.sin(Math.toRadians(segStartPoint)) + height/2, holeRadius, bcPaintSolid);
                segStartPoint -= segment;
            }
        }
    }
}
