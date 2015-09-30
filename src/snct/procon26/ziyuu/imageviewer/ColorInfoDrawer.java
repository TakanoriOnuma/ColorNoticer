package snct.procon26.ziyuu.imageviewer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;

public class ColorInfoDrawer {
    private int   mColorInfo;
    private float mCursorSize;
    private Paint mBlackPaint;
    private Paint mWhitePaint;
    private Paint mFillWhitePaint;
    private Paint mTextPaint;

    // 演算用バッファ
    private int[] mRGB = new int[3];
    private int[] mHSV = new int[3];

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

    public void drawColorInfo(Canvas canvas, Point pos) {
        Point pt = new Point(pos.x, pos.y);
        canvas.drawCircle(pt.x, pt.y, mCursorSize, mBlackPaint);
        canvas.drawCircle(pt.x, pt.y, mCursorSize + mBlackPaint.getStrokeWidth(), mWhitePaint);

        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float height = -fontMetrics.top + fontMetrics.bottom;

        mRGB[0] = (mColorInfo >> 16) & 0xff;
        mRGB[1] = (mColorInfo >> 8)  & 0xff;
        mRGB[2] =  mColorInfo        & 0xff;
        String text = String.format("%3d, %3d, %3d", mRGB[0], mRGB[1], mRGB[2]);
        float  textWidth = mTextPaint.measureText(text);

        pt.x += mCursorSize;
        canvas.drawRect(pt.x, pt.y, pt.x + textWidth, pt.y + height, mFillWhitePaint);
        canvas.drawText(text, pt.x, pt.y - fontMetrics.top, mTextPaint);

//        ColorTransfar.transRGBtoHSV(mRGB, mHSV);
//        text = String.format("%3d, %3d, %3d", mHSV[0], mHSV[1], mHSV[2]);
//        textWidth = mTextPaint.measureText(text);
//        canvas.drawRect(pt.x + 100, pt.y + 100 - 50 + 50, pt.x + 100 + textWidth, pt.y + 120 + 50, mFillWhitePaint);
//        canvas.drawText(text, pt.x + 100, pt.y + 100 + 50, mTextPaint);
    }
}
