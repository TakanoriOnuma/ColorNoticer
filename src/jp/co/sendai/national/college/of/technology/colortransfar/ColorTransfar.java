package jp.co.sendai.national.college.of.technology.colortransfar;

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
                    // マスクする必要があるなら、黒にして次へ
                    if(mColorFilter.isMask(rgb[0], rgb[1], rgb[2], mFrameNum, mPeriodFrameNum)) {
                        rgbs[yp] = 0xff000000;
                        continue;
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
}
