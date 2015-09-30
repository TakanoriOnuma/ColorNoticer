package snct.procon26.ziyuu.imageviewer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class ColorInfoDrawer {
    private int   mColorInfo;
    private float mCursorSize;
    private Paint mBlackPaint;
    private Paint mWhitePaint;
    private Paint mFillWhitePaint;
    private Paint mTextPaint;

    public ColorInfoDrawer() {
        init();
    }

    private void init() {
        mCursorSize = 20.0f;

        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
        mBlackPaint.setStyle(Paint.Style.STROKE);
        mBlackPaint.setStrokeWidth(3);

        mWhitePaint = new Paint();
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setStyle(Paint.Style.STROKE);
        mWhitePaint.setStrokeWidth(3);

        mFillWhitePaint = new Paint();
        mFillWhitePaint.setColor(Color.WHITE);
        mFillWhitePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(30);
    }

    public void setColorInfo(int colorInfo) {
        mColorInfo = colorInfo;
    }

    public void drawColorInfo(Canvas canvas, Point pt) {
        canvas.drawCircle(pt.x, pt.y, mCursorSize, mBlackPaint);
        canvas.drawCircle(pt.x, pt.y, mCursorSize + mBlackPaint.getStrokeWidth(), mWhitePaint);

        int rgb = mColorInfo;
        String text = String.format("%d, %d, %d, %d", ((rgb >> 24) & 0xff), ((rgb >> 16) & 0xff), ((rgb >> 8) & 0xff), (rgb & 0xff));
        canvas.drawRect(pt.x + 100, pt.y + 100 - 50, pt.x + 360, pt.y + 120, mFillWhitePaint);
        canvas.drawText(text, pt.x + 100, pt.y + 100, mTextPaint);
    }
}
