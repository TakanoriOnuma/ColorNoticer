package jp.co.sendai.national.college.of.technology;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OverLayView extends View {
    private Bitmap mBitmap     = null;
    private Rect   mBitmapRect = null;
    private Rect   mViewRect   = new Rect(0, 0, 0, 0);

    private int   mColorInfo;
    private Point mCursorPos;
    private float mCursorSize;
    private Paint mBlackPaint;
    private Paint mWhitePaint;
    private Paint mFillWhitePaint;
    private Paint mTextPaint;

    public OverLayView(Context context) {
        super(context);
        init();
    }

    public OverLayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCursorPos  = new Point();
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

    // ゲッター
    public Point getCursorPoint() {
        return mCursorPos;
    }
    public Rect getViewRect() {
        return mViewRect;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        mViewRect.set(0, 0, getWidth(), getHeight());
        mCursorPos.x = getWidth() / 2;
        mCursorPos.y = getHeight() / 2;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mBitmapRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap != null) {
            canvas.drawBitmap(mBitmap, mBitmapRect, mViewRect, null);

            drawColorInfo(canvas);
        }
    }

    private void drawColorInfo(Canvas canvas) {
        canvas.drawCircle(mCursorPos.x, mCursorPos.y, mCursorSize, mBlackPaint);
        canvas.drawCircle(mCursorPos.x, mCursorPos.y, mCursorSize + mBlackPaint.getStrokeWidth(), mWhitePaint);

        int rgb = mColorInfo;
        String text = String.format("%d, %d, %d, %d", ((rgb >> 24) & 0xff), ((rgb >> 16) & 0xff), ((rgb >> 8) & 0xff), (rgb & 0xff));
        canvas.drawRect(mCursorPos.x + 100, mCursorPos.y + 100 - 50, mCursorPos.x + 360, mCursorPos.y + 120, mFillWhitePaint);
        canvas.drawText(text, mCursorPos.x + 100, mCursorPos.y + 100, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCursorPos.x = (int)event.getX();
        mCursorPos.y = (int)event.getY();

        mCursorPos.x = (mCursorPos.x < 0) ? 0 : (mCursorPos.x < mViewRect.right)  ? mCursorPos.x : mViewRect.right;
        mCursorPos.y = (mCursorPos.y < 0) ? 0 : (mCursorPos.y < mViewRect.bottom) ? mCursorPos.y : mViewRect.bottom;

        return true;
    }
}
