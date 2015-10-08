package snct.procon26.ziyuu.imageviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ImageViewer extends View {
    private Bitmap mBitmap     = null;
    private Rect   mBitmapRect = null;
    private Rect   mViewRect   = new Rect(0, 0, 0, 0);

    private Point mCursorPos = new Point();
    private ColorInfoDrawer mColorInfoDrawer = null;

    public ImageViewer(Context context) {
        super(context);
    }

    public ImageViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // セッター
    public void setColorInfoDrawer(ColorInfoDrawer colorInfoDrawer) {
        mColorInfoDrawer = colorInfoDrawer;
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
            // 緊急処置
            mViewRect.set(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(mBitmap, mBitmapRect, mViewRect, null);

            if(mColorInfoDrawer != null) {
                mColorInfoDrawer.drawColorInfo(canvas, mCursorPos);
            }
        }
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
