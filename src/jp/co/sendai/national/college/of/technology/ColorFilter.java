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
}
