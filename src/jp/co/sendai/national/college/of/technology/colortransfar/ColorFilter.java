package jp.co.sendai.national.college.of.technology.colortransfar;

public class ColorFilter implements IColorFilter {
    private int mSaturation = 0;
    private int mHueStart   = -60;
    private int mHueEnd     = -60;

    private int mRedRate   = 100;
    private int mGreenRate = 100;
    private int mBlueRate  = 100;

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
    public void setRedRate(int redRate) {
        mRedRate = redRate;
    }
    public void setGreenRate(int greenRate) {
        mGreenRate = greenRate;
    }
    public void setBlueRate(int blueRate) {
        mBlueRate = blueRate;
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

        float max = max(r, g, b);
        float min = min(r, g, b);

        float contrast = (max - min) / max * 255;

        // 差が小さいなら彩度Sも小さいので計算しない
        if(contrast <= mSaturation) {
            return false;
        }

        float hue;
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

    private static float max(int a, int b, int c) {
        if(a > b) {
            return (a > c) ? a : c;
        }
        return (b > c) ? b : c;
    }
    private static float min(int a, int b, int c) {
        if(a < b) {
            return (a < c) ? a : c;
        }
        return (b < c) ? b : c;
    }

    @Override
    public void filtering(int[] rgb) {
        rgb[0] = rgb[0] * mRedRate   / 100;
        rgb[1] = rgb[1] * mGreenRate / 100;
        rgb[2] = rgb[2] * mBlueRate  / 100;
    }
}
