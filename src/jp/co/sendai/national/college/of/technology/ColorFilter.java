package jp.co.sendai.national.college.of.technology;

public class ColorFilter {
    public static void colorFilter(int rgb[], int alpha, int red, int green, int blue) {
        alpha &= 0xff;
        red   &= 0xff;
        green &= 0xff;
        blue  &= 0xff;
        for(int i = 0; i < rgb.length; i++) {
            rgb[i] = alpha << 24 | red << 16 | green << 8 | blue;
        }
    }

    public static void mask(int[] rgb, byte[] yuv420sp, int width, int height,
            int saturation, int hueStart, int hueEnd) {
        int frameSize = width * height;
        for(int j = 0, yp = 0; j < height; j++) {
            int u = 0;
            int v = 0;
            int uvp = frameSize + (j >> 1) * width;

            for(int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int)yuv420sp[yp])) - 16;
                if(y < 0) {
                    y = 0;
                }
                if((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                // 指定範囲内なら黒でマスクする
                if(isMask(y, u, v, saturation, hueStart, hueEnd)) {
                    rgb[yp] = 0xff000000;
                }
            }
        }
    }

    // hueStartからhueEndの色相かを調べる
    // ただし彩度についても考慮する
    private static boolean isMask(int y, int u, int v, int saturation, int hueStart, int hueEnd) {
        int y1192 = 1192 * y;
        int r = y1192 + 1634 * v;
        int g = y1192 - 833 * v - 400 * u;
        int b = y1192 + 2066 * u;

        float max = max(r, g, b);
        float min = min(r, g, b);

        float contrast = (max - min) / max * 255;

        // 差が小さいなら彩度Sも小さいので計算しない
        if(contrast <= saturation) {
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

        if(hue >= hueStart && hue <= hueEnd) {
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
}
