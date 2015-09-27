package jp.co.sendai.national.college.of.technology;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class OverLayView extends View {
    private Bitmap mBitmap = null;
    private Rect   mBitmapRect = null;

    public OverLayView(Context context) {
        super(context);
    }

    public OverLayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mBitmapRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap != null) {
            Rect viewRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(mBitmap, mBitmapRect, viewRect, null);
        }
    }
}
