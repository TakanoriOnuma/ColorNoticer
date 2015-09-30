package snct.procon26.ziyuu.colortransfar;

import snct.procon26.ziyuu.functions.MyFunction;

public class ColorFilter implements IColorFilter {
    private int mSaturation = 0;
    private int mHueStart   = -60;
    private int mHueEnd     = -60;

    // コンストラクタ
    public ColorFilter() {
    }
    public ColorFilter(int saturation, int hueStart, int hueEnd) {
        mSaturation = saturation;
        mHueStart   = hueStart;
        mHueEnd     = hueEnd;
    }

    // セッター
    public void setSaturation(int saturation) {
        mSaturation = saturation;
    }
    public void setHueStart(int hueStart) {
        mHueStart = hueStart;
    }
    public void setHueEnd(int hueEnd) {
        mHueEnd = hueEnd;
    }

    public static void colorFilter(int rgb[], int alpha, int red, int green, int blue) {
        alpha &= 0xff;
        red   &= 0xff;
        green &= 0xff;
        blue  &= 0xff;
        for(int i = 0; i < rgb.length; i++) {
            rgb[i] = alpha << 24 | red << 16 | green << 8 | blue;
        }
    }

    // hueStartからhueEndの色相かを調べる
    // ただし彩度についても考慮する
    @Override
    public boolean isMask(int r, int g, int b, int frameNum, int periodFrameNum) {
        if(frameNum < periodFrameNum / 2)
            return false;

        int max = MyFunction.max(r, g, b);
        if(max == 0) {
            return false;
        }

        int min = MyFunction.min(r, g, b);
        int contrast = 255 * (max - min) / max;

        // 差が小さいなら彩度Sも小さいので計算しない
        if(contrast <= mSaturation) {
            return false;
        }

        int hue;
        if(max == r) {
            hue = 60 * (g - b) / (max - min);
        }
        else if(max == g) {
            hue = 60 * (b - r) / (max - min) + 120;
        }
        else {
            hue = 60 * (r - g) / (max - min) + 240;
        }

        if(hue >= mHueStart && hue <= mHueEnd) {
            return true;
        }
        return false;
    }
}
