package jp.co.sendai.national.college.of.technology;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class OverLayView extends View {
    private Bitmap mBitmap     = null;
    private Rect   mBitmapRect = null;
    private Rect   mViewRect   = new Rect(0, 0, 0, 0);

    public OverLayView(Context context) {
        super(context);
    }

    public OverLayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        mViewRect.set(0, 0, getWidth(), getHeight());
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mBitmapRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap != null) {
            canvas.drawBitmap(mBitmap, mBitmapRect, mViewRect, null);
        }
    }
}
