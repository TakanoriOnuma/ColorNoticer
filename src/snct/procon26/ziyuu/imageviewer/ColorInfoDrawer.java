package snct.procon26.ziyuu.imageviewer;

import snct.procon26.ziyuu.colortransfar.ColorTransfar;
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
        mFillWhitePaint.setColor(0x77ffffff);
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
        ColorTransfar.transRGBtoHSV(mRGB, mHSV);

        String text = String.format("R:%03d H:%03d", mRGB[0], mHSV[0]);
        float  textWidth = mTextPaint.measureText(text);

        pt.x += mCursorSize;
        pt.y += mCursorSize;
        canvas.drawRect(pt.x - 2, pt.y - 2, pt.x + textWidth + 2, pt.y + 4 * height + 2, mFillWhitePaint);
        canvas.drawRect(pt.x - 2, pt.y - 2, pt.x + textWidth + 2, pt.y + 4 * height + 2, mBlackPaint);

        String colorName = getColorName(mHSV);
        text = String.format("色名:%s", colorName);
        canvas.drawText(text, pt.x, pt.y - fontMetrics.top, mTextPaint);

        pt.y += height;
        text = String.format("R:%3d H:%3d", mRGB[0], mHSV[0]);
        canvas.drawText(text, pt.x, pt.y - fontMetrics.top, mTextPaint);

        pt.y += height;
        text = String.format("G:%3d S:%3d", mRGB[1], mHSV[1]);
        canvas.drawText(text, pt.x, pt.y - fontMetrics.top, mTextPaint);

        pt.y += height;
        text = String.format("B:%3d V:%3d", mRGB[2], mHSV[2]);
        canvas.drawText(text, pt.x, pt.y - fontMetrics.top, mTextPaint);
    }

    public String getColorName(int[] hsv) {
        if(hsv[0] < 30) {
            return "赤";
        }
        else if(hsv[0] < 90) {
            return "黄";
        }
        else if(hsv[0] < 150) {
            return "緑";
        }
        else if(hsv[0] < 210) {
            return "水色";
        }
        else if(hsv[0] < 270) {
            return "青";
        }
        else if(hsv[0] < 330) {
            return "紫";
        }
        else {
            return "赤";
        }
    }
}
