package com.wolfteck.smoothtouch2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GridDiagram extends View {

    private int strokeWidth = 1;
    private Paint gPaintSolid;
    private Paint gPaintDotted;
    private RectF rectF;
    private int rows;
    private int cols;
    private float dx;
    private float dy;
    private float angle;
    private float drillSize;

    public GridDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        gPaintSolid = new Paint();
        gPaintSolid.setStyle(Paint.Style.STROKE);
        gPaintSolid.setStrokeWidth(strokeWidth);

        gPaintDotted = new Paint();
        gPaintDotted.setStyle(Paint.Style.STROKE);
        gPaintDotted.setStrokeWidth(strokeWidth);
        gPaintDotted.setPathEffect(new DashPathEffect(new float[] {10,15},0));
    }

    public void setRows(int rows) {
        this.rows = rows;
        invalidate();
    }

    public void setCols(int cols) {
        this.cols = cols;
        invalidate();
    }

    public void setDx(float dx) {
        this.dx = dx;
        invalidate();
    }

    public void setDy(float dy) {
        this.dy = dy;
        invalidate();
    }

    public void setAngle(float angle) {
        this.angle = (float) Math.toRadians(angle);
        invalidate();
    }

    public void setDrillSize(float drillSize) {
        this.drillSize = drillSize;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(rows > 0 && 0 < cols) {

            float realFirstRowMaxHeight = ((cols - 1) * dx) * (float) Math.sin(angle);
            float realFirstColMaxHeight = ((rows - 1) * dy) * (float) Math.sin(angle + Math.toRadians(90));
            float realHeight = realFirstRowMaxHeight + realFirstColMaxHeight + drillSize;

            float realFirstRowMaxR = ((cols - 1) * dx) * (float) Math.cos(angle);
            float realFirstColMaxL = ((rows - 1) * dy) * (float) Math.cos(angle + Math.toRadians(90));
            float realWidth = realFirstRowMaxR - realFirstColMaxL + drillSize;

            float scale = Math.min((canvas.getHeight() - strokeWidth) / realHeight, canvas.getWidth() / realWidth);

            float firstRowMaxHeight = scale * realFirstRowMaxHeight;
            float firstColMaxHeight = scale * realFirstColMaxHeight;
            float firstRowMaxR = scale * realFirstRowMaxR;
            float firstColMaxL = scale * realFirstColMaxL;

            float drillRadius = scale * drillSize / 2;

            float shiftX = scale * dx;
            float shiftY = scale * dy;

            canvas.translate(drillRadius - firstColMaxL, canvas.getHeight() - drillRadius - strokeWidth);
            canvas.scale(1,-1);

            // canvas.drawLine() is broken
            // https://issuetracker.google.com/issues/36945767
            Path path = new Path();
            path.moveTo(0, 0);
            path.lineTo(firstRowMaxR, 0);
            path.moveTo(0, 0);
            path.lineTo(firstRowMaxR, firstRowMaxHeight);
            canvas.drawPath(path, gPaintDotted);

            for(int i = 0; i < cols; i++) {
                for(int j = 0; j < rows; j++) {
                    double x = (i * shiftX) * Math.cos(angle) + (j * shiftY) * Math.cos(angle + Math.toRadians(90));
                    double y = (i * shiftX) * Math.sin(angle) + (j * shiftY) * Math.sin(angle + Math.toRadians(90));
                    canvas.drawCircle((float) x,(float) y, drillRadius, gPaintSolid);
                }
            }
        }
    }
}
