package snct.procon26.ziyuu.colortransfar;

import snct.procon26.ziyuu.functions.MyFunction;


public class ColorTransfar {
    private IColorFilter mColorFilter = null;
    private IColorValueTransfar mColorValueTransfar = null;

    private int mFrameNum       = 0;
    private int mPeriodFrameNum = 10;

    public ColorTransfar() {
    }
    public ColorTransfar(IColorFilter colorFilter) {
        mColorFilter = colorFilter;
    }
    public ColorTransfar(IColorValueTransfar colorValueTransfar) {
        mColorValueTransfar = colorValueTransfar;
    }

    public void setColorFilter(IColorFilter colorFilter) {
        mColorFilter = colorFilter;
    }
    public void setColorValueTransfar(IColorValueTransfar colorValueTransfar) {
        mColorValueTransfar = colorValueTransfar;
    }
    // NV21からRGBに変換する関数
    // 参考URL: http://www.41post.com/3470/programming/android-retrieving-the-camera-preview-as-a-pixel-array
    public void decodeYUV420SP(int[] rgbs, byte[] yuv420sp, int width, int height) {
        // フレーム数のカウントアップ
        mFrameNum = (mFrameNum + 1) % mPeriodFrameNum;

        int frameSize = width * height;
        int[] rgb = new int[3];
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

                int y1192 = 1192 * y;
                rgb[0] = y1192 + 1634 * v;
                rgb[1] = y1192 - 833 * v - 400 * u;
                rgb[2] = y1192 + 2066 * u;

                if(mColorFilter != null) {
                    // マスクする必要があるなら、半透明のマスクをかける
                    // （1bitシフトでalpha=0.5の黒マスクになる）
                    if(mColorFilter.isMask(rgb[0], rgb[1], rgb[2], mFrameNum, mPeriodFrameNum)) {
                        rgb[0] >>= 1;
                        rgb[1] >>= 1;
                        rgb[2] >>= 1;
                    }
                }
                if(mColorValueTransfar != null) {
                    mColorValueTransfar.colorTranslate(rgb);
                }

                // 262143 = 2^18 - 1 = 16^4 * 2^2 - 1
                rgb[0] = (rgb[0] < 0) ? 0 : (rgb[0] < 262143) ? rgb[0] : 262143;
                rgb[1] = (rgb[1] < 0) ? 0 : (rgb[1] < 262143) ? rgb[1] : 262143;
                rgb[2] = (rgb[2] < 0) ? 0 : (rgb[2] < 262143) ? rgb[2] : 262143;

                rgbs[yp] = 0xff000000 | ((rgb[0] << 6) & 0xff0000) | ((rgb[1] >> 2) & 0xff00) | ((rgb[2] >> 10) & 0xff);
            }
        }
    }

    // NV21からRGBに変換する関数を参考に
    // 指定した座標の色を取得する
    public static int getColor(byte[] yuv420sp, int width, int height, int x, int y) {
        int frameSize = width * height;
        int[] rgb = new int[3];
        for(int j = 0, yp = 0; j < height; j++) {
            int u = 0;
            int v = 0;
            int uvp = frameSize + (j >> 1) * width;

            for(int i = 0; i < width; i++, yp++) {
                if((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                if(x != i || y != j) {
                    continue;
                }

                int _y = (0xff & ((int)yuv420sp[yp])) - 16;
                if(_y < 0) {
                    _y = 0;
                }

                int y1192 = 1192 * _y;
                rgb[0] = y1192 + 1634 * v;
                rgb[1] = y1192 - 833 * v - 400 * u;
                rgb[2] = y1192 + 2066 * u;

                // 262143 = 2^18 - 1 = 16^4 * 2^2 - 1
                rgb[0] = (rgb[0] < 0) ? 0 : (rgb[0] < 262143) ? rgb[0] : 262143;
                rgb[1] = (rgb[1] < 0) ? 0 : (rgb[1] < 262143) ? rgb[1] : 262143;
                rgb[2] = (rgb[2] < 0) ? 0 : (rgb[2] < 262143) ? rgb[2] : 262143;

                return 0xff000000 | ((rgb[0] << 6) & 0xff0000) | ((rgb[1] >> 2) & 0xff00) | ((rgb[2] >> 10) & 0xff);
            }
        }
        return -1;
    }

    public static void transRGBtoHSV(int[] rgb, int[] hsv) {
        int max = MyFunction.max(rgb[0], rgb[1], rgb[2]);
        if(max == 0) {
            hsv[0] = 0;
            hsv[1] = 0;
            hsv[2] = 0;
            return;
        }
        int min = MyFunction.min(rgb[0], rgb[1], rgb[2]);

        if(max == min) {
            hsv[0] = 0;
        }
        else if(max == rgb[0]) {
            hsv[0] = 60 * (rgb[1] - rgb[2]) / (max - min);
            if(hsv[0] < 0) {
                hsv[0] += 360;
            }
        }
        else if(max == rgb[1]) {
            hsv[0] = 60 * (rgb[2] - rgb[0]) / (max - min) + 120;
        }
        else {
            hsv[0] = 60 * (rgb[0] - rgb[1]) / (max - min) + 240;
        }

        hsv[1] = 100 * (max - min) / max;
        hsv[2] = 100 * max / 255;
    }
}
