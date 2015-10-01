package snct.procon26.ziyuu.imageviewer;

import snct.procon26.ziyuu.colortransfar.ColorTransfar;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
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
        mFillWhitePaint.setColor(0x88ffffff);
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

        int size = 100;
        int width  = 2 * size * 1732 / 2000;

        mRGB[0] = (mColorInfo >> 16) & 0xff;
        mRGB[1] = (mColorInfo >> 8)  & 0xff;
        mRGB[2] =  mColorInfo        & 0xff;
        ColorTransfar.transRGBtoHSV(mRGB, mHSV);

        String text = getColorName(mHSV);
        float  textWidth = mTextPaint.measureText(text);

        pt.x += mCursorSize;
        pt.y += mCursorSize;
        canvas.drawRect(pt.x - 2, pt.y - 2, pt.x + width + 2, pt.y + height + 2, mFillWhitePaint);
        canvas.drawRect(pt.x - 2, pt.y - 2, pt.x + width + 2, pt.y + height + 2, mBlackPaint);

        canvas.drawText(text, pt.x + (width - textWidth) / 2, pt.y - fontMetrics.top, mTextPaint);

        Point pivot = new Point(pt.x + width / 2, pt.y + (int)height + size + 3);
        drawColorChart(canvas, pivot, size);

        Paint paint = new Paint();
        paint.setColor(Color.rgb(mRGB[0], mRGB[1], mRGB[2]));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        pivot.x += (mRGB[1] - mRGB[2]) * 1732 * size / (2000 * 255);
        pivot.y += (-mRGB[0] + (mRGB[1] + mRGB[2]) / 2) * size / 255;
        canvas.drawCircle(pivot.x, pivot.y, mCursorSize / 2, paint);
    }

    private void drawColorChart(Canvas canvas, Point pivot, int size) {
        Paint paint = new Paint();
        paint.setColor(0x77ffffff);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        Path path = new Path();
        path.moveTo(pivot.x, pivot.y - size);       // 赤

        int halfWidth  = size * 1732 / 2000;
        int halfHeight = size / 2;
        path.lineTo(pivot.x + halfWidth, pivot.y - halfHeight);     // 黄色
        path.lineTo(pivot.x + halfWidth, pivot.y + halfHeight);     // 緑
        path.lineTo(pivot.x, pivot.y + size);                       // 水色
        path.lineTo(pivot.x - halfWidth, pivot.y + halfHeight);     // 青
        path.lineTo(pivot.x - halfWidth, pivot.y - halfHeight);     // 紫

        canvas.drawPath(path, paint);

        // 矢印の描画
        Paint arrowPaint = new Paint();
        arrowPaint.setStrokeWidth(3);
        arrowPaint.setColor(Color.RED);
        drawArrow(canvas, pivot, new Point(pivot.x, pivot.y - size), arrowPaint);
        arrowPaint.setColor(Color.GREEN);
        drawArrow(canvas, pivot, new Point(pivot.x + halfWidth, pivot.y + halfHeight), arrowPaint);
        arrowPaint.setColor(Color.BLUE);
        drawArrow(canvas, pivot, new Point(pivot.x - halfWidth, pivot.y + halfHeight), arrowPaint);
    }

    private void drawArrow(Canvas canvas, Point startPt, Point endPt, Paint paint) {
        canvas.drawLine(startPt.x, startPt.y, endPt.x, endPt.y, paint);
    }

    public String getColorName(int[] hsv) {
        if(hsv[0] < 30) {
            return "赤色";
        }
        else if(hsv[0] < 90) {
            return "黄色";
        }
        else if(hsv[0] < 150) {
            return "緑色";
        }
        else if(hsv[0] < 210) {
            return "水色";
        }
        else if(hsv[0] < 270) {
            return "青色";
        }
        else if(hsv[0] < 330) {
            return "紫色";
        }
        else {
            return "赤色";
        }
    }
}
