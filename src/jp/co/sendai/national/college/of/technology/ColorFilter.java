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

    public static void mask(int[] rgb, byte[] data, int width, int height,
            int saturation, int hueStart, int hueEnd) {
        int size = width*height;
        int offset = size;
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = data[i  ]&0xff;
            y2 = data[i+1]&0xff;
            y3 = data[width+i  ]&0xff;
            y4 = data[width+i+1]&0xff;

            u = data[offset+k  ]&0xff;
            v = data[offset+k+1]&0xff;
            u = u-128;
            v = v-128;

            if(isMask(y1, u, v, saturation, hueStart, hueEnd))
                rgb[i  ] = 0xff000000;
            if(isMask(y2, u, v, saturation, hueStart, hueEnd))
                rgb[i+1] = 0xff000000;
            if(isMask(y3, u, v, saturation, hueStart, hueEnd))
                rgb[width+i  ] = 0xff000000;
            if(isMask(y4, u, v, saturation, hueStart, hueEnd))
                rgb[width+i+1] = 0xff000000;

            if (i!=0 && (i+2)%width==0)
                i+=width;
        }
    }

    // hueStartからhueEndの色相かを調べる
    // ただし彩度についても考慮する
    private static boolean isMask(int y, int u, int v, int saturation, int hueStart, int hueEnd) {
        float r = y + 1.402f * v;
        float g = y + -0.344f * u - 0.714f * v;
        float b = y + 1.772f * u;

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

    private static float max(float a, float b, float c) {
        if(a > b) {
            return (a > c) ? a : c;
        }
        return (b > c) ? b : c;
    }
    private static float min(float a, float b, float c) {
        if(a < b) {
            return (a < c) ? a : c;
        }
        return (b < c) ? b : c;
    }
}
